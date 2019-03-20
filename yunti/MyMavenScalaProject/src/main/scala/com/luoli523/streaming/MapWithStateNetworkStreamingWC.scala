package com.luoli523.streaming

import org.apache.spark._
import org.apache.spark.streaming._

object MapWithStateNetworkStreamingWC {


	def main(args : Array[String]) {
		if(args.length < 2) {
			println("Usage: MapWithStateNetworkStreamingWC <network_port> <batch_seconds>")
			System.exit(1)
		}
		val networkPort = args(0).toInt
		val batchSeconds = args(1).toInt

		val conf = new SparkConf().setAppName("NetworkWordCount")
		val ssc = new StreamingContext(conf, Seconds(batchSeconds))
		ssc.checkpoint("/tmp/spark_checkpoint")

		val initialRDD = ssc.sparkContext.parallelize(List(("hello",1),("spark", 1)))

		val lines = ssc.socketTextStream("localhost", networkPort)
		val words = lines.flatMap(_.split(" "))
		val pairs = words.map(word => (word, 1))
		val wordCounts = pairs.reduceByKey(_+_)

		// Update the cumulative count using mapWithState
	    // This will give a DStream made of state (which is the cumulative count of the words)
	    val mapWithStateFunc = (key:String, one:Option[Int], state:State[Int]) => {
	    	val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
	    	val output = (key, sum)
	    	state.update(sum)
	    	output
	    }

	    val stateDstream = pairs.mapWithState(
	    	StateSpec.function(mapWithStateFunc).initialState(initialRDD))
	    stateDstream.print()

		ssc.start()
		ssc.awaitTermination()
	}
}