package com.luoli523.spark

import org.apache.spark.sql.SparkSession

object SimpleApp {
  def main(args: Array[String]) {
    if( args.length < 1) {
      println("Usage: SimpleApp \\${YOUR_SPARK_HOME}")
      return
    }
    val home = args{0}
    val logFile = s"${home}/README.md" // Should be some file on your system
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val logData = spark.read.textFile(logFile).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println(s"Lines with a: $numAs, Lines with b: $numBs")
    spark.stop()
  }
}

