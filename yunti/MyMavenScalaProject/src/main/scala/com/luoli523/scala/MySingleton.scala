package com.luoli523.scala

object MySingleton {
	@volatile
	private var instance : Seq[String] = null

	def getInstance() : Seq[String] = {
		if(instance == null) {
			synchronized {
				if(instance == null) {
					instance = Seq("a", "b", "c")
				}
			}
		}
		instance
	}
}