package com.luoli523.spark.streaming

//import org.apache.spark._
//import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger
//import org.apache.spark.streaming._

object KafkaHdfsAuditIngestion {

  def main(args: Array[String]): Unit = {

    if (args.length < 2) {
      System.err.println("Usage: com.luoli523.spark.streaming.KafkaHdfsAuditIngestion <kafka bootstrap server list> <kafka topic> [optional: checkpoint path]")
      System.err.println("\teg: com.luoli523.spark.streaming.KafkaHdfsAuditIngestion host1:port,host2:port,host3:port my_topic")
      System.err.println("\teg: com.luoli523.spark.streaming.KafkaHdfsAuditIngestion host1:port,host2:port,host3:port my_topic hdfs://nn1:port/path/to/ckpt")
      System.exit(1)
    }

    val bootstrap = args(0)
    val topic = args(1)
    var ckptPath = "hdfs://tl0/user/li.luo/streaming_ckpt_test"

    if (args.length > 2) {
      ckptPath = args(2)
    }

    StreamingUtilies.setStreamingLogLevels()

    val spark = SparkSession
      .builder
      .appName("KafkaHdfsAuditIngestion")
      .getOrCreate()

    import spark.implicits._

    val hdfsAuditStreamingInputDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", bootstrap)
      .option("subscribe", topic)
      .load()

    hdfsAuditStreamingInputDF.printSchema()

    val auditDF = hdfsAuditStreamingInputDF.select("value")

    val query = auditDF.writeStream
      .outputMode("append")
      .format("console")
      .option("checkpointLocation", ckptPath)
      .trigger(Trigger.Continuous("5 second"))
      .start()

    query.awaitTermination()

  }

}
