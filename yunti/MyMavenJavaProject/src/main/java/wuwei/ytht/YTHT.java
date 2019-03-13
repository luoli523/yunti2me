package wuwei.ytht;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class YTHT  implements Tool {
  public static final String VERSION = "0.4.2";//add multi output file support

	public static final Log LOG = LogFactory.getLog(YTHT.class);
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
	static {
		NUMBER_FORMAT.setGroupingUsed(false);
	}
	private final static String MERGE_TEMP = "_merge_tmp";
  private static long maxMergeFileSize = -1;
  private static boolean sortInput = false;
  private final static String FILE_SEPERATOR = ";";
	private JobConf conf;

  // file pattern
  //  part-00000
  //  part-00000.deflate
  //  attempt_201003291206_202753_r_000159_0
  //  attempt_201003291206_311567_r_000128_0.deflate
  static String part = "(part-([rm]-)?\\d{5,6})";
  static String attempt = "(attempt_\\d{12}_\\d{4,7}_[rm]_\\d{6}_\\d)";
  static String suffix = "(\\.(deflate|gz|bz2))?";
  static String default_p = "(" + part + "|" +  attempt + ")" + suffix;
  static final Pattern defaultPattern = Pattern.compile(default_p);
  static Pattern filePattern;

	public YTHT(JobConf conf) {
		setConf(conf);
	}

	public Configuration getConf() {
		return this.conf;
	}

	public void setConf(Configuration conf) {
		if (conf instanceof JobConf) {
			this.conf = (JobConf) conf;
		} else {
			this.conf = new JobConf(conf);
		}
	}

	static class MergeFilesMapper implements
			Mapper<LongWritable, Text, WritableComparable<?>, Text> {

		private Path root;
		private Path bak;
		private FileSystem fs;
		private Configuration conf;

		public void configure(JobConf job) {
			root = new Path(job.get("ytht.root.dir"));
			bak = new Path(job.get("ytht.backup.dir"));
			LOG.info("root dir: " + root);
			LOG.info("backup dir: " + bak);
			
			conf = job;
			try {
				fs = FileSystem.get(job);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void map(LongWritable key, Text value,
				OutputCollector<WritableComparable<?>, Text> output,
				Reporter reporter) throws IOException {
			String[] fileList = value.toString().split(FILE_SEPERATOR);
      if (fileList.length > 2) {
        String dir = fileList[0];
        reporter.setStatus("merging dir: " + dir);
        LOG.info("Dir for merge: " + dir);
        mergeDir(root, fileList, bak, fs, conf, reporter);
      }
		}

		public void close() throws IOException {

		}
	}

	private static CompressionType getCompressionType(SequenceFile.Reader in) {
		if(in.isCompressed()) 
			if(in.isBlockCompressed())
				return CompressionType.BLOCK;
			else
				return CompressionType.RECORD;
		else
			return CompressionType.NONE;
	}
	
	@SuppressWarnings("rawtypes")
	private static void copyKeyValue(SequenceFile.Reader in, SequenceFile.Writer out,
			Configuration conf) throws IOException {
		WritableComparable key = ReflectionUtils.newInstance(in.getKeyClass().asSubclass(
				WritableComparable.class), conf);
		Writable val = ReflectionUtils.newInstance(in.getValueClass().asSubclass(
				Writable.class), conf);
		try {
			while(in.next(key, val)) {
				out.append(key, val);
			}
		} catch (IOException ee) {
			ee.printStackTrace();
			System.out.println("Got IOException when reading seq file. " +
					"Maybe EOFException, but continue...");
		}
	}
	
	private static void mergeSequenceFile(FileSystem srcFS, Path srcDir, FileStatus[] contents,
			FileSystem dstFS, Path dstFile, Configuration conf, Reporter reporter) throws IOException {

	  int len = contents.length;
		SequenceFile.Reader in = null;
		for (int i = 0; i < contents.length; i++) {
			if (!contents[i].isDir()) {
				in = new SequenceFile.Reader(
						srcFS, contents[i].getPath(), conf);
				break;
			}
		}
		
		if (in == null)
			return;
		
		SequenceFile.Writer out = SequenceFile.createWriter(
				dstFS, conf, dstFile, in.getKeyClass(), in.getValueClass(),
				getCompressionType(in), in.getCompressionCodec());
		
		try {
			for (int i = 0; i < contents.length; i++) {
				if (!contents[i].isDir() && contents[i].getLen() > 0) {
					in = new SequenceFile.Reader(
							srcFS, contents[i].getPath(), conf);
					try {
						reporter.setStatus("merging dir: " 
								+ srcDir + " (" + (i+1) + "/" + len + ")");
						copyKeyValue(in, out, conf);
					} finally {
						in.close();
					}
				}
			}
		} finally {
			out.close();
		}
	}
	
	private static void mergeCompressedFile(FileSystem srcFS, Path srcDir, FileStatus[] contents,
			FileSystem dstFS, Path dstFile, Configuration conf,
			CompressionCodec codec, Reporter reporter) throws IOException {		
		OutputStream out = codec.createOutputStream(dstFS.create(dstFile));
		
		long maxSize = conf.getLong("ytht.compressed.file.maxsize", -1);
		long totalSize = 0L;

		try {
			int totalFiles = contents.length;
			for (int curFile = 0; curFile < contents.length; curFile++) {
				if (!contents[curFile].isDir() && contents[curFile].getLen() > 0) {
					long fileSize = contents[curFile].getLen();
					totalSize += fileSize;
					if (maxSize != -1 && totalSize > maxSize && fileSize != totalSize) {
						// generate new file
						out.close();
						
						totalSize = fileSize;
						dstFile = getNextFileName(dstFile);
						out = codec.createOutputStream(dstFS.create(dstFile));
					}
				
					InputStream in = codec.createInputStream(srcFS
							.open(contents[curFile].getPath()));
					try {
						reporter.setStatus("merging dir: " 
								+ srcDir + " (" + (curFile+1) + "/" + totalFiles + ")");
						IOUtils.copyBytes(in, out, conf, false);
					} finally {
						in.close();
					}
				}
			}
		} finally {
			out.close();
		}
	}
	
	static Path getNextFileName(Path dstFile) {
		String prev = dstFile.getName();
		String next = null;
		
		Pattern p1 = Pattern.compile("part-([rm]-)?(\\d{5,6})" +  // part-xxx
				"(\\.(deflate|gz|bz2))?"); // suffix
		Pattern p2 = Pattern.compile(
				"attempt_\\d{12}_\\d{4,7}_[rm]_(\\d{6})_\\d" + // attempt_xxx
				"(\\.(deflate|gz|bz2))?"); // suffix
		Matcher m1 = p1.matcher(prev);
		Matcher m2 = p2.matcher(prev);
		if (m1.find()) {
			String num = m1.group(2);
			int n = Integer.parseInt(num) + 1;
			NUMBER_FORMAT.setMinimumIntegerDigits(num.length());
			next = prev.replace(num,NUMBER_FORMAT.format(n));
		} else if (m2.find()) {
			String num = m2.group(1);
			int n = Integer.parseInt(num) + 1;
			NUMBER_FORMAT.setMinimumIntegerDigits(num.length());
			next = prev.replace("_" + num, "_" + NUMBER_FORMAT.format(n));
		}
		return new Path(dstFile.getParent(), next);
	}

  static boolean validFile(FileStatus f, String custom_p) {
    if (f == null || f.isDir()) {
      return false;
    }
    return validFileNamePattern(f.getPath().getName(), custom_p);
  }

  static boolean validFileNamePattern(String path, String custom_p) {

    if (custom_p != null && !custom_p.equals("")) {
      filePattern = Pattern.compile("(" + default_p + ")|(" + custom_p + ")");
    } else {
      filePattern = defaultPattern;
    }
    return filePattern.matcher(path).matches();
  }

	private static void mergeNormalFile(FileSystem srcFS, Path srcDir, FileStatus[] contents,
			FileSystem dstFS, Path dstFile, Configuration conf, Reporter reporter)
			throws IOException {
		OutputStream out = dstFS.create(dstFile);

		try {
			int len = contents.length;
			for (int i = 0; i < contents.length; i++) {
				if (!contents[i].isDir()) {
					InputStream in = srcFS.open(contents[i].getPath());
					try {
						reporter.setStatus("merging dir: " 
								+ srcDir + " (" + (i+1) + "/" + len + ")");
						IOUtils.copyBytes(in, out, conf, false);
					} finally {
						in.close();
					}
				}
			}
		} finally {
			out.close();
		}
	}

	/*
	 * input path must be subdir of root root: /to/merge/root input:
	 * /to/merge/root/dir1/dir2 backup: /to/backup
	 * 
	 * 1. merge dir input to file: 
	 *     /to/backup/_merge_tmp/dir1/dir2/part-00000 
	 * 2. rename dir 
	 *     /to/merge/root/dir1/dir2
	 *    to
	 *     /to/backup/dir1/dir2 
	 * 3. rename dir
	 *     /to/backup/_merge_tmp/dir1/dir2 
	 *    to 
	 *     /to/merge/root/dir1/dir2
	 */
	private static void mergeDir(Path root, String[] fileList, Path backupRoot,
			FileSystem fs, Configuration conf, Reporter reporter) throws IOException {
	  if(fileList.length<3){
	    return;
	  }
	  
		Path output = null;
		Path backupPath = null;
		
		// first one is the directory
		Path input = new Path(fileList[0]);
		//secondary is the merge target file name;
		Path testFile = new Path(fileList[1]);
		String testFileName = testFile.getName();
		
		if (input.equals(root)) {
			output = new Path(new Path(backupRoot, MERGE_TEMP), input.getName()+"/"+testFileName);
			backupPath = new Path(backupRoot, input.getName());
		} else {
			String relative = input.toString().substring(
					root.toString().length() + 1);
			Path relPath = new Path(relative);
			output = new Path(new Path(backupRoot, MERGE_TEMP), relPath+"/"+testFileName);
			backupPath = new Path(backupRoot, relPath);
		}
    FileStatus srcFileStatus = fs.getFileStatus(testFile);
    if(srcFileStatus==null){
      LOG.info("srcfile has been deleted! path="+testFileName);
      return ;
    }

		FSDataInputStream i = null;
		try {
			i = fs.open(testFile);
		} catch(IOException e) {
			// file or directory removed, skip this dir
			reporter.getCounter("YTHT", "dir skipped").increment(1);
			return;
		}
		
		boolean isSeqFile = false;
		try {
			if(i.readByte() == 'S' && i.readByte() == 'E' && i.readByte() == 'Q') {
				isSeqFile = true;
			}
		} catch (EOFException e) {
			isSeqFile = false;
		}
		
    Path[] filePaths = new Path[fileList.length - 1];
    for (int f = 1; f < fileList.length; f++) {
      filePaths[f - 1] = new Path(fileList[f]);
    }
    FileStatus[] fileContents = fs.listStatus(filePaths);
    if (fileContents.length < 2) {
      return;
    }
		try {
			if(isSeqFile) {
				LOG.info("Merging sequence file.");
				mergeSequenceFile(fs, input, fileContents, fs, new Path(output, testFileName), 
						conf, reporter);
			} else {
				CompressionCodecFactory compressionCodecs = new CompressionCodecFactory(conf);
				final CompressionCodec codec = compressionCodecs.getCodec(testFile);
				if (codec == null) {
					LOG.info("Merging text file.");
					mergeNormalFile(fs, input, fileContents, fs, new Path(output, testFileName), 
						conf, reporter);
				} else {
					LOG.info("Merging compressed file.");
					mergeCompressedFile(fs, input, fileContents, fs, new Path(output, testFileName), 
							conf, codec, reporter);
				}
			}
		} catch (Exception e) {
			// Maybe file to be write in _merge_tmp is created by other attempt.
			// Catch the execption and skip this directory
			LOG.info("Got exception when merge dir.");
			e.printStackTrace();
			reporter.getCounter("YTHT", "dir skipped").increment(1);
			return;
		}

    for(Path p:filePaths){
      Path bakPath=new Path(backupPath, p.getName());
      fs.mkdirs(bakPath.getParent());
      LOG.info("move orgifile "+p+" backup to "+bakPath);
      fs.rename(p, bakPath);
    }
    
    //for compressed file, there may be many merged files.
    FileStatus[] outputFiles = fs.listStatus(output);
    for (FileStatus o : outputFiles) {
      Path newSrcPath = new Path(input, o.getPath().getName());
      fs.rename(o.getPath(), newSrcPath);
      fs.setTimes(newSrcPath, srcFileStatus.getModificationTime(), -1);
      LOG.info("move mergeFile "+o.getPath()+" to "+new Path(input, testFile));
    }
    
    reporter.getCounter("YTHT", "dir merged").increment(1);
	}
	

	/*
	 * dir can contain files like: 
	 * _logs, 
	 * part-00000 
	 * part-00001 
	 * ... 
	 * part-000xx
	 * 
	 * parameter speedUpFiles is used to reduce calls of listStatus
	 */
	private static boolean needMerge(Path dir, FileSystem fs, Configuration conf,
			List<FileStatus> speedUpFiles, List<Path> oneFile) throws IOException {
		if (speedUpFiles != null) {
			speedUpFiles.clear();
		}

		long avgSize = conf.getLong("ytht.average.filesize", 64 * 1024 * 1024);

		FileStatus[] files = fs.listStatus(dir);
		if(files == null) { 
			// dir deleted
			return false;
		}
		long totalSize = 0;
		int num = 0;
		String firstFile = null;
		for (FileStatus f : files) {
			String fileName = f.getPath().getName();
			if (fileName.equals("_logs")) {
				continue;
			}

			if (!validFile(f, conf.get("ytht.file.pattern"))) {
				// if not all files are part-000xx
				// use speed up file list
				if (speedUpFiles != null) {
					speedUpFiles.addAll(Arrays.asList(files));
				}
				return false;
			} else {
				if (firstFile == null) {
					firstFile = fileName;  
				}
				num++;
				totalSize += f.getLen();
			}
		}
		if (num < 2 || (totalSize / num > avgSize)) // files too large, no need
													// to merge
			return false;

		if(conf.get("ytht.dir.pattern") != null && 
				!conf.get("ytht.dir.pattern").equals("")) {
			Pattern dir_p = Pattern.compile(conf.get("ytht.dir.pattern"));
			if (!dir_p.matcher(dir.getName()).matches()) // dir pattern not matches 
				return false;
		}
		
		LOG.info("dir " + dir.toString() + " (" + num + ") need merge");
		// trick: set a file such as part-00000
		if (oneFile != null) {
			oneFile.clear();
			oneFile.add(new Path(dir, firstFile));
		}
		return true;
	}

	private void generateDirListForMerge(Path dir, FileSystem fs, JobConf conf,
			List<Path> toMerge) throws IOException {
		List<FileStatus> speedup = new LinkedList<FileStatus>();
		List<Path> oneFile = new ArrayList<Path>(1); 

		if (needMerge(dir, fs, conf, speedup, oneFile)) {
			toMerge.add(oneFile.get(0));
		} else {
			if (!speedup.isEmpty()) {
				for (FileStatus f : speedup) {
					if (f.isDir()) {
						generateDirListForMerge(f.getPath(), fs, conf, toMerge);
					}
				}
			}
		}
	}

	private void setup(JobConf conf, String targetPath, String backupPath)
			throws IOException {
		if(conf.get("ytht.file.pattern") != null && 
				conf.get("ytht.compressed.file.maxsize") != null ) {
			throw new IOException("ytht.compressed.file.maxsize and " +
					"file pattern cann't set together");
		}
		
		conf.setJobName("YTHT-"+VERSION+"-"+targetPath);

		FileSystem fs = FileSystem.get(conf);
		Path root = new Path(targetPath);
		root = root.makeQualified(fs);
		Path bak = new Path(backupPath);
		bak = bak.makeQualified(fs);
		
		if (!fs.getFileStatus(root).isDir()) {
			throw new IOException("Target path for YTHT is not a dir.");
		}
		if (fs.exists(bak)) {
			if (!fs.getFileStatus(bak).isDir()) {
				throw new IOException("Backup path for YTHT is not a dir.");
			}
		}
		fs.mkdirs(new Path(bak, MERGE_TEMP)); 

		List<Path> toMerge = new LinkedList<Path>();
		generateDirListForMerge(root, fs, conf, toMerge);
		
		int dirNum = toMerge.size();
		if (dirNum == 0) {
			LOG.info("No dir need to be merged. Exiting...");
		} else {
			// set up a job
			String randomId = Integer.toString(new Random()
					.nextInt(Integer.MAX_VALUE), 36);

			Path jobDir = new Path(bak, "_ytht_files_" + randomId);
			Path input = new Path(jobDir, "to_merge");
			
			generateInputFile(conf, fs, toMerge, input);

			Path output = new Path(jobDir, "result");
			FileInputFormat.setInputPaths(conf, input);
			FileOutputFormat.setOutputPath(conf, output);
			conf.set("ytht.root.dir", root.toString());
			conf.set("ytht.backup.dir", bak.toString());
			conf.set("ytht.job.dir", jobDir.toString());
			conf.setMapSpeculativeExecution(false);
			conf.setNumReduceTasks(0);
			conf.setMapperClass(MergeFilesMapper.class);
			conf.setInt("mapred.task.timeout", 0);
		}
	}

	/**
	 * generate merge job input file.<br/>
	 * e.g:<br/>
	 * to merge dir:
	 * dir1/part0001
	 *      part0002
	 *      part0003
	 * dir2/part0001
	 *      part0002
	 *      part0003
	 * 
	 * will generate input file $backdir/_merge_tmp/input-0 which includes:</br>
	 * dir1;dir1/part0001;dir1/part0002;dir1/part0003
	 * dir2;dir2/part0001;dir2/part0002;dir2/part0003
	 * 
	 * @param conf
	 * @param fs
	 * @param toMerge
	 * @param input
	 * @throws IOException
	 */
  private void generateInputFile(JobConf conf, FileSystem fs,
      List<Path> toMerge, Path input) throws IOException {
    List<String> seperateFileList = new ArrayList<String>();
    
    maxMergeFileSize = conf.getLong("ytht.max.merge.file.size", -1);
    sortInput = conf.getBoolean("ytht.sort.input.file", false);
    boolean forRaid=conf.getBoolean("ytht.for.raid",false);
    for (Path firstFile:toMerge) {
      Path parentDir = firstFile.getParent();
      //if max.merge.file.size is set, check blockSize for raid
      if (maxMergeFileSize > 0 && forRaid) {
        FileStatus[] firstStatus = fs.listStatus(firstFile);
        if (firstStatus.length > 0
            && maxMergeFileSize < firstStatus[0].getBlockSize() * 2) {
          LOG.warn("skip merge dir=" + parentDir
              + " because max.merge.file.size=" + maxMergeFileSize
              + " less than 2*blockSize which can't be raid");
          continue;
        }
      } 
      
      //construct merge list
      StringBuilder subList=new StringBuilder(parentDir.toString());
      List<FileStatus> filesStatus = Arrays.asList(fs.listStatus(parentDir));
      
      if(sortInput){
        Collections.sort(filesStatus, new FileComparator());
      }
      
      long totalSize = 0;
      int subFileCount=0;
      for (FileStatus status : filesStatus) {
        if (status.getPath().getName().equals("_logs")|| status.isDir()) {
          continue;
        }
        if(maxMergeFileSize>0){
          if (sortInput && status.getLen() >= maxMergeFileSize){
            break;
          }
          if (totalSize != 0
              && (totalSize + status.getLen()) > maxMergeFileSize) {
            if (subFileCount > 1) {
              // if there is only one file, it may be a big file and dosen't
              // need merge!
              seperateFileList.add(subList.toString());
            }
            totalSize = 0;
            subFileCount = 0;
            subList = new StringBuilder(parentDir.toString());
          }
        }
        subFileCount++;
        totalSize += status.getLen();
        subList.append(";").append(status.getPath());
      }
      
      if (subFileCount > 1) {
        seperateFileList.add(subList.toString());
      }
      
    }
    
    int num = conf.getNumMapTasks();
    int dirNum = seperateFileList.size();
    for (int i = 0; i < num; i++) {
      PrintWriter out = new PrintWriter(
          fs.create(new Path(input, "input-" + i)));
      for (int j = i; j < dirNum; j += num) {
        out.println(seperateFileList.get(j));
      }
      out.close();
    }
  }
  
  /**
   * File size comparator:<br>
   * <ol>
   * <li>dir == dir
   * <li>dir > any file
   * <li>between files compare use file.getLen()
   * </ol>
   *
   */
  static class FileComparator implements Comparator<FileStatus> {
    public int compare(FileStatus o1, FileStatus o2) {
      if(o1.isDir()||o2.isDir()){
        return o1.isDir() ? (o2.isDir() ? 0 : 1) : -1;
      }
      return o1.getLen() - o2.getLen() > 0 ? 1 : (o1.getLen() - o2.getLen() < 0
          ? -1
          : 0);
    }
  }
  
	private void cleanup(JobConf conf, String backupPath) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		fs.delete(new Path(backupPath, MERGE_TEMP), true);
		String jobDir = conf.get("ytht.job.dir");
		if(jobDir != null) {
			fs.delete(new Path(jobDir), true);
		}
	}
	
	private List<String> parseArgument(String[] args) {
		List<String> other_args = new ArrayList<String>();		
		for (int idx = 0; idx < args.length; idx++) {
			if ("-m".equals(args[idx])) {
				if (++idx ==  args.length) {
		            throw new IllegalArgumentException("map number not specified in -m");
		        }
				try {
					int maps = Integer.parseInt(args[idx]);
					if (maps <= 0) {
						throw new IllegalArgumentException("map number must be larger than 0");
					}
					conf.setNumMapTasks(maps);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Integer expected but got " + args[idx]);
				}
			} else if ("-p".equals(args[idx])) {
				if (++idx ==  args.length) {
		            throw new IllegalArgumentException(
		            		"dir pattern for merge not specified in -p");
		        }
				conf.set("ytht.dir.pattern", args[idx]);
			} else if ("-f".equals(args[idx])) {
				if (++idx ==  args.length) {
		            throw new IllegalArgumentException(
		            		"file pattern for merge not specified in -f");
		        }
				conf.set("ytht.file.pattern", args[idx]);
			} else {
				other_args.add(args[idx]);
			}
		}
		if (other_args.size() < 2) {
			throw new IllegalArgumentException("not enough arguments");
		}
		return other_args;
	}

	public int run(String[] args) throws Exception {
		try {
			List<String> other_args = parseArgument(args);

			if (conf.getNumMapTasks() > 2) {
				LOG.info("Max running threads: " + conf.getNumMapTasks());
			}

			// TODO: if no backup dir is set, using /path/to/merge/../_ytht_backup
			// TODO: parameter settings
			setup(conf, other_args.get(0), other_args.get(1));
			if (conf.get("ytht.job.dir") != null) {
				JobClient.runJob(conf);
			}
			cleanup(conf, args[1]);
		} catch (IllegalArgumentException e) {
			printUsage();
			return -1;
		} catch (IOException e) {
			LOG.error("YTHT: get exception when merging files");
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	private void printUsage() {
		System.out.println("Usage: YTHT " +
				"[ -p dir_pattern ] [-f file_pattern] [ -m map_num ] " +
				"dir backup_dir ");
	}

	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(YTHT.class);
		int res = ToolRunner.run(new YTHT(conf), args);
		System.exit(res);
	}

}
