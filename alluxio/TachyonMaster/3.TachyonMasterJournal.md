#TachyonMaster Journal日志记录（checkpoint,editlog）


Tachyon Master将文件系统的原信息（MetaData）缓存在Master的内存中，以提供高速的文件系统访问，这些信息会随时Client端对文件系统的各种操作，以及Worker机器不停的上线下线等实际情况实时的发生调整和更新。当Master一直处在running状态的时候，一切都相安无事。但如果因为一些原因要重启Tachyon Master（比如版本升级，或者master宕机重启等），Tachyon Master需要在重启后将文件系统状态恢复到重启之前的状态，这就需要一种机制，能够在master running的时候，实时的将其状态的修改持久化到磁盘或者其他持久化存储介质中，以供重启后帮助master恢复状态。

Tachyon Master用来提供这个功能的组件叫做Journal。

Tachyon Journal跟Hadoop NameNode的EditLog非常类似，提供如下机制以持久化保存内存状态：

* 通过将内存的状态全量写入到一个Checkpoint文件来保存某个时刻Master内存状态的全量snapshot
* 运行过程中，通过将所有```改变Master状态```的操作都同步写入到log文件中（Tachyon Master叫做log.out，namenode中叫做editlog），并按照一定规律（比如时间interval，log文件大小上限等）进行log文件的滚动
* 从上一个checkpoint开始，加上所有checkpoint后的log文件，就能够全量记录所有Master状态的修改记录
* 当Master重启时，通过先回放checkpoint文件，然后按照顺序回放所有的log文件，即可将Master内存状态恢复到重启前一刻的状态。

以上所有，组合起来，就是Tachyon中的Journal系统。下面来详细介绍Journal的各个组件。


## Journal概况

如 [TachyonMaster整体架构](TachyonMaster整体架构.md) 这篇中所记录的，在Tachyon Master中，有多个功能组件，比如FileSystemMaster，BlockMaster等，每个组件都对应有自己独立的Journal环境。实际上在tachyon中，```一个Journal描述的其实是一个用来记录某种组件（比如FileSystemMaster，或者BlockMaster等）的所有checkpoint和edit log的目录```。

通常Tachyon Master将Journal记录在本地磁盘上，通过如下配置选项来制定Tachyon Master的Journal根目录：

    tachyon.master.journal.folder=/path/to/journal/dir  
    
    默认情况下，这个目录为${tachyon.home}/journal/
    当然这个journal根目录也可以指定到UnderFs中，比如HDFS等，只需要将该选项配置成 hdfs://namenode:port/path/to/journal/ 即可
    

Tachyon Master中有如下几种Journal，在这里称每一种Journal为一个Journal Context：

    FileSystemMaster Journal: 用来记录文件系统namespace相关的operate log（oplog），默认路径为${tachyon.master.journal.folder}/FileSystemMaster
    BlockMaster Journal: 用来记录所有的block原信息相关的log，如blockid，blocksize等，默认路径为${tachyon.master.journal.folder}/FileSystemMaster
    RawTableMaster Journal: 用来记录所有的RawTable原信息相关的log，默认路径为${tachyon.master.journal.folder}/RawTableMaster
    LineageMaster Journal: 用来记录所有的Lineage原信息相关的log，默认路径为${tachyon.master.journal.folder}/LineageMaster

### Journal类

Journal类实际上表示一个```Journal Context```，实际就是一个目录（如上面所说的```${tachyon.master.journal.folder}/FileSystemMaster```目录），一个Journal Context中，包含有三种不同的日志文件：

* checkpoint.data文件 ：既Tachyon Master的中某个XXMaster（```FileSystemMaster```，```BlockMaster```...）的checkpoint日志文件，它表示某一个时刻Tachyon Master状态的一个checkpoint
* log.out文件：这个文件是XXMaster正在写的current log
* completed目录：这是一个目录，由于current log不能一直不停的写下去，Tachyon Master会根据某些规则进行RollLog的操作，Roll出来的log文件按照编号被放置在这个completed目录中，如```log.1```,```log.2```等等。

想要恢复状态，master只需要按照如下顺序回放（replay）Journal log即可：

1. replay checkpoint文件
2. replay completed/log.1 -> completed/log.2 -> ...
3. replay log.out (current log)

### JournalReader类

当Tachyon Master启动的时候，需要对按照上述顺序对Journal log进行回放。而回放过程实际上就是从journal log中读出一条一条的JournalEntry，将这些Entry一个一个的apply到Master中去的过程。而读取Journal log的class就是```JournalReader```。

在Tachyon0.8.2及之前的版本，Tachyon的Journal格式一直都是Json格式，Json格式的Journal日志如下所示：

    {"mSequenceNumber":122,"mType":"INODE_DIRECTORY","mParameters":{"pinned":false,"creationTimeMs":1449481802092,"name":"","childrenIds":[249],"id":0,"persisted":false,"parentId":-1,"lastModificationTimeMs":1449545825298}}
    {"mSequenceNumber":123,"mType":"INODE_DIRECTORY","mParameters":{"pinned":false,"creationTimeMs":1449545825299,"name":"luoli","childrenIds":[369098751,402653183],"id":249,"persisted":false,"parentId":0,"lastModificationTimeMs":1449548050777}}
    {"mSequenceNumber":124,"mType":"INODE_FILE","mParameters":{"pinned":false,"creationTimeMs":1449545842607,"blocks":[352321536,352321537],"length":1073741824,"completed":true,"ttl":-1,"parentId":249,"blockSizeBytes":536870912,"name":"1Gfile","cacheable":true,"id":369098751,"persisted":false,"lastModificationTimeMs":1449545844353}}
    {"mSequenceNumber":126,"mType":"INODE_DIRECTORY_ID_GENERATOR","mParameters":{"sequenceNumber":250,"containerId":0}}
    
以上日志是FileSystemMaster的checkpoint.data日志，从日志从可以还原出该namespace的snapshot。

* 第一条日志表示根节点（ROOT），是一个INODE_DIRECTORY
* 第二条日志表示一个叫```luoli```的目录，其父节点是ROOT
* 第三条日志表示一个叫做```1Gfile```的文件（mType:INODE_FILE）,该文件包含两个block，id分别是369098751,402653183，通过查找BlockMaster中的相应blockid，即可直到这两个block的size和所在worker等信息

因此，该checkpoint描述的namespace情况即为如下现状：

    $bin/tachyon tfs ls /luoli
    1024.00MB 12-08-2015 11:37:22:607  In Memory      /luoli/1Gfile

    
从0.8.2之后，Tachyon Journal log换成了更加高效的ProtocBuf格式。到这份笔记记录的时间，ProtocBuf格式的Journal日志版本还没有release。

JournalReader提供如下接口：

![image](../images/JournalReader_interface.png)


### JournalWriter类

当Tachyon Master启动并load journal log结束后，通常需要对当前已经恢复的内存状态做因此最新的checkpoint，以获取一个最新的文件系统状态snapshot。同时在Tachyon Running过程中，也需要定期的对journal log进行Roll，以确保每一个log文件都不会太大。这些工作工作都是由```JournalWriter```类来完成。


### JournalTailer类

从```JournalReader```接口能够看出，该class能够提供获取checkpoint inputStream，获取下一个log文件的inputStream等操作，而真正回放这些inputStream中的Entry的工作，则是由```JournalTailer```完成的。

因此，在Tachyon Master启动时，每个用单独Journal Context的Service（BlockMaster，FileSystemMaster等）都会启动一个线程，并通过```JournalTailer```来按照顺序读取checkpoint和log文件，并一个一个Entry的apply到Tachyon Master上。


### Journal相关配置选项

    tachyon.master.journal.folder=${tachyon.home}/journal/
    tachyon.master.journal.formatter.class=tachyon.master.journal.ProtoBufJournalFormatter
    tachyon.master.journal.log.size.bytes.max=10MB
    tachyon.master.journal.tailer.shutdown.quiet.wait.time.ms=5000
    tachyon.master.journal.tailer.sleep.time.ms=1000