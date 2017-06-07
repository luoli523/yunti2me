# 大数据底层技术中级知识图谱

## Hadoop Common
  * 了解protocol buffer
    * 掌握`.proto`文件定义规范
    * `[练习]`完成java版代码生成和基础接口编程([官方文档](https://developers.google.com/protocol-buffers/docs/javatutorial))
    * hadoop RPC中涉及到的数据交换协议定义（.proto文件。common，hdfs，yarn，mapreduce模块中都有定义）
    * 了解hadoop代码中编译`.proto`文件的`pom`定义
    * `[练习]`自己动手编译hadoop common模块，注意由`.proto`文件生成的`java`代码
  * 深入理解hadoop RPC框架代码逻辑(`org.apache.hadoop.ipc.*`)
  * `[练习]`自己动手写一个自定义的`.proto`文件定义，并通过protoc生成message java代码，并通过hadoop rpc框架进行Server/Client调用，client调用rpc接口，server端返回`hello hadoop`，并在client端打印。
  * hadoop metrics2框架
    * 熟悉hadoop metrics2整个框架和机制([context官方配置文档](http://hadoop.apache.org/docs/r2.7.3/hadoop-project-dist/hadoop-common/Metrics.html))([架构文档](http://blog.cloudera.com/blog/2012/10/what-is-hadoop-metrics2))
    * `[练习]自己动手配置metrics，数据写入到FileContext`
  * 所有common相关关键配置([core-default.xml](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/core-default.xml))

## HDFS
  * namenode所有对外rpc接口(`ClientProtocol/DatanodeProtocol/NamenodeProtocol/RefreshAuthorizationPolicyProtocol/RefreshUserMappingsProtocol/RefreshCallQueueProtocol/GenericRefreshProtocol/GetUserMappingsProtocol/HAServiceProtocol`等)
  * namenode内部关键管理类（`NameNode,FSNamesystem,NameNodeRpcServer,FSDirectory`）
  * namenode内存数据结构（namespace/BlocksMaps/几个block状态队列）
  * namenode editlog机制(JournalSet/NNStorage)
  * namenode auditlog，stateChangeLog
  * Standby Checkpoint机制
  * DataNode Storage体系结构
    * `FSVolumeSet/FSVolume/FSDir`
    * `Storage/StorageDirectory`
  * DataNode BlockPool体系(BPOfferService/BPServiceActor)
  * DataNode响应读写请求的XceiverServer/XcieverThread
  * haadmin主备切换shell接口和背后原理
  * 所有hdfs关键配置([hdfs-default.xml](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml))
  * `[练习]`自己动手debug远程namenode，熟悉其内部线程和运行原理
  * `[练习]`准备PPT详述HDFS内部架构，机制，接口
  * HDFS Federation机制([官方文档](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/Federation.html))
  * HDFS ViewFS([官方文档](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/ViewFs.html))
  * HDFS权限机制([官方文档](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html))
  * HDFS Quota机制([官方文档](http://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-hdfs/HdfsQuotaAdminGuide.html))


## YARN
  * bin/yarn命令行工具熟悉([官方文档](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/YarnCommands.html))
  * ResourceManager对外RPC接口和Service机制(`ClientRMService/ApplicationMasterService等`)
  * ResourceManager内部关键组件
    * AsyncDispatcher
    * Scheduler
      * FairScheduler([官方文档](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/FairScheduler.html))
      * CapacityScheduler([官方文档](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/CapacityScheduler.html))
      * NodeLabel机制([官方文档](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/NodeLabel.html))
    * ClientRMService
    * ApplicationMasterService
    * RMAppManager
  * ResourceManager HA机制([官方文档](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/ResourceManagerHA.html))
  * NodeManager
    * ContainerManager
      * Container
      * Application
      * AuxServices
      * logaggregation
    * LinuxContainerExecutor 
    * NodeResourceMonitor
    * NodeManager重启([官方文档](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/NodeManagerRestart.html))
  * `[练习]`自己动手debug远程ResourceManager，熟悉其内部线程和运行原理
  * `[练习]`准备PPT详述YARN内部架构，机制，接口
  

## Hive
  * Hive代码结构和主要架构([参考文档](https://cwiki.apache.org/confluence/display/Hive/DeveloperGuide))
  * Hive UDF(UDTF,UDAF)熟悉([参考文档](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF#LanguageManualUDF-Built-inAggregateFunctions(UDAF)))
  * 自定义Hive UDF([参考文档](https://cwiki.apache.org/confluence/display/Hive/HivePlugins))
  * 主要代码熟悉
    * Hive Serde库
      * ORC
      * Parquet
      * CSV
      * Avro
    * Hive MetaStore
    * Hive QL
  * Hive周边系统
    * HiveServer2
    * Hive Cli
  * `[练习]`准备PPT详述Hive 

## Spark
  * Spark代码编译([官方文档](http://spark.apache.org/docs/latest/building-spark.html))
  * Spark RPC(Netty)机制和核心架构了解
  * Spark RDD内部细节了解
    * partition
    * compute
    * dependency
  * SparkContext
  * Spark DAGScheduler
  * Spark TaskScheduler
  * Spark内存管理机制(UnifiedMemoryManager)
  * BlockManager
  * Spark Executor
  * Spark调优([参考文档](http://spark.apache.org/docs/latest/tuning.html))
  * Spark Configuration([参考文档](http://spark.apache.org/docs/latest/configuration.html))
  * Spark Streaming
    * [流式计算史诗级论文101](https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-101)
    * [流式计算史诗级论文102](https://www.oreilly.com/ideas/the-world-beyond-batch-streaming-102)
    * Spark Streaming编程手册([官方文档](http://spark.apache.org/docs/latest/streaming-programming-guide.html))
    * Checkpoint
    * SparkStreaming+Kafka
  * SparkSQL
    * SparkSQL编程指南([官方文档](http://spark.apache.org/docs/latest/sql-programming-guide.html))
    * Spark Catalyst
 
## HBase
  * HBase new feature及代码编译流程
* HBase 代码及架构
  * HMaster
  * HRegionServer
  * HFile
  * HLog
  * LSM
  * zookeeper
  * protocol
  * thrift
  * rest
  * common
  * tools
  * shell
  * client
  * coprocessor
* HBase LSM tree([网上资料](http://blog.csdn.net/liuxiao723846/article/details/52971511))
* HBase 调优([官方文档](http://hbase.apache.org/book.html#performance))
* HBase 小文件存储([官方文档](http://hbase.apache.org/book.html#hbase_mob))
* HBase 数据批量导入([官方文档](http://hbase.apache.org/book.html#_bulk_load))
* HBase rowkey设计([官方文档](http://hbase.apache.org/book.html#rowkey.design))
* HBase 安全机制([官方文档](http://hbase.apache.org/book.html#security))
* HBase 多语言支持([官方文档](http://hbase.apache.org/book.html#external_apis)) 
