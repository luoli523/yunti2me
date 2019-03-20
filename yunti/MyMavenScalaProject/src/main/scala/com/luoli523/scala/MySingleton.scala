package com.luoli523.scala

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast

object MySingleton {
	@volatile private var instance : Broadcast[Seq[String]] = null

	def getInstance(sc : SparkContext) : Broadcast[Seq[String]] = {
		if(instance == null) {
			synchronized {
				if(instance == null) {
					val wordlist = Seq("a", "b", "c")
					instance = sc.broadcast(wordlist)
				}
			}
		}
		instance
	}
	
}