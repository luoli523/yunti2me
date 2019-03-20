package com.luoli523.streaming

import org.apache.spark._
import org.apache.spark.streaming._

object SimpleStreamingWC {

	def main(args : Array[String]) {
		if(args.length < 2) {
			println("Usage: SimpleStreamingWC <network_port> <batch_seconds>")
			System.exit(1)
		}
		val networkPort = args(0).toInt
		val batchSeconds = args(1).toInt

		val conf = new SparkConf().setAppName("NetworkWordCount")
		val ssc = new StreamingContext(conf, Seconds(batchSeconds))

		val lines = ssc.socketTextStream("localhost", networkPort)
		val words = lines.flatMap(_.split(" "))
		val pairs = words.map(word => (word, 1))
		val wordCounts = pairs.reduceByKey(_+_)

		wordCounts.print()

		ssc.start()
		ssc.awaitTermination()
	}
}