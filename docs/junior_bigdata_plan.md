# 大数据底层技术初级知识图谱

## HDFS
  * HDFS架构
  * HDFS集群搭建
    * 单机版环境搭建([官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SingleCluster.html))
    * 集群版环境搭建（[官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/ClusterSetup.html)）
    * HDFS关键参数配置（[官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml)）
    * HDFS集群启停
  * HDFS Shell熟悉（[官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/FileSystemShell.html)）
  * dfsadmin命令接口熟悉
  * FileSystem编程接口练习
    * 实现读本地文件写入HDFS
    * 实现从HDFS读取文件写入本地
    * 实现一个基于HDFS的`tail -f`功能

## YARN
  * YARN架构([官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-site/YARN.html))
  * YARN集群搭建
    * YARN关键参数配置([官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-common/yarn-default.xml))
    * mapred关键参数配置([官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/mapred-default.xml))
    * YARN集群启停
  * MapReduce计算模型原理和编程接口([官方文档](http://hadoop.apache.org/docs/r2.7.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html))
  * 自己动手写一个MR job（实现`sum groupbykey`功能），并提交到自己搭建的集群
  * 自己动手写一个Yarn App（实现从每个container往hdfs上写一个该containerid的文件），并提交到自己搭建的集群
  

## Hive
  * Hive整体架构([官方文档](https://cwiki.apache.org/confluence/display/Hive/Design))
  * Hive运行环境搭建([官方文档](https://cwiki.apache.org/confluence/display/Hive/AdminManual+Installation))
    * Hive相关参数配置熟悉([官方文档](https://cwiki.apache.org/confluence/display/Hive/AdminManual+Configuration))
    * 搭建Hive MetaStore([官方文档](https://cwiki.apache.org/confluence/display/Hive/AdminManual+MetastoreAdmin))
  * HQL
    * Hive基本语法([官方文档](https://cwiki.apache.org/confluence/display/Hive/LanguageManual))
    * DDL语法熟悉([官方文档](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL))
    * DML语法熟悉([官方文档](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DML))
    * FileFormat初步了解([官方文档](https://cwiki.apache.org/confluence/display/Hive/FileFormats))

## Spark
  * Spark原理([官方文档](http://spark.apache.org/docs/latest/quick-start.html))
  * Spark提交环境搭建([官方文档](http://spark.apache.org/docs/latest/submitting-applications.html))
    * Spark submit（client模式，cluster模式）
  * Spark编程接口([官方文档](http://spark.apache.org/docs/latest/programming-guide.html))
    * transform函数
    * action函数
    * 数据读写函数
  * 自己动手写一个spark job（scala，java）
    * 从HDFS读取文本数据，将数据分解成单词序列，并统计包含‘spark’的单词有多少
  * 了解SparkStreaming([官方文档](http://spark.apache.org/docs/latest/streaming-programming-guide.html))
  * 了解SparkSQL([官方文档](http://spark.apache.org/docs/latest/sql-programming-guide.html))
 
## HBase
  * HBase架构([牛逼文档](http://blog.zahoor.in/2012/08/hbase-hmaster-architecture/))
  * 搭建HBase集群([官方文档](http://hbase.apache.org/book.html#quickstart))
    * hbase关键参数配置
  * HBase数据模型([官方文档](http://hbase.apache.org/book.html#datamodel))
    * Table
    * Row
    * ColumnFamily
    * Column Qualifier
    * Collmn
    * Cell
    * TimeStamp
  * HBase shell操作熟悉([官方文档](http://hbase.apache.org/book.html#shell))
