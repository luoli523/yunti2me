package com.luoli523.streaming

import org.apache.spark._
import org.apache.spark.streaming._

object StatefulNetworkStreamingWC {


	def main(args : Array[String]) {
		if(args.length < 2) {
			println("Usage: StatefulNetworkStreamingWC <network_port> <batch_seconds>")
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

		def updateFunc(newValues : Seq[Int], runningCount: Option[Int]) : Option[Int] = {
			if (newValues.isEmpty) {
				return runningCount
			}
			val newSum = newValues.reduce(_+_)
			val sum = runningCount.getOrElse(0) + newSum
			Some(sum)
		}

		def updateFuncUseMatch(newValues : Seq[Int], runningCount: Option[Int]) : Option[Int] = {
			newValues match {
				//case Nil  => runningCount // case List() not OK
				case Seq() => runningCount // case List() not OK
				case _ => {
					val newSum = newValues.reduce(_+_)
					val sum = runningCount.getOrElse(0) + newSum
					Some(sum)
				}
			}
		}

		val updateFuncUseIterator = (iter : Iterator[(String, Seq[Int], Option[Int])]) => {
			val func = (values: Seq[Int], oldValue : Option[Int]) => {
				val sum = values.sum
				Option(oldValue.getOrElse(0) + sum)
			}	
			iter.flatMap(t => func(t._2, t._3).map(s => (t._1, s)))
		}

		val partitioner = new HashPartitioner(ssc.sparkContext.defaultParallelism)

		// val runningCounts = pairs.updateStateByKey[Int](updateFunc _, partitioner, initialRDD)
		val runningCounts = pairs.updateStateByKey[Int](updateFuncUseMatch _, partitioner, initialRDD)
		// val runningCounts = pairs.updateStateByKey[Int](updateFuncUseIterator, partitioner, true, initialRDD)
		runningCounts.print()

		/*
		// Update the cumulative count using mapWithState
	    // This will give a DStream made of state (which is the cumulative count of the words)
	    val mappingFunc = (word: String, one: Option[Int], state: State[Int]) => {
	      val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
	      val output = (word, sum)
	      state.update(sum)
	      output
	    }

	    val stateDstream = wordCounts.mapWithState(
	      StateSpec.function(mappingFunc).initialState(initialRDD))
	    stateDstream.print()
		*/

		ssc.start()
		ssc.awaitTermination()
	}
}