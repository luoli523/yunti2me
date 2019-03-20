package com.luoli523.spark

import org.apache.spark.sql.{SaveMode, SparkSession}

object TxtParseToTable {

	// Data format like below
	// ID|BUSSINESS_NO|REPORT_CODE|NAME|DEBIT_KEY|DEBIT_VALUE
	// 356|8a8501c9467fe5ec01468a47adab4a31|1468330281025|2011-06|全部负债余额|0.00
	case class CreditInfo(id:Int, 
    businessNo:String, 
    reportCode:Long, 
    name:String, 
    uncreditKey:String,
    uncreditValue:Double)
	
	def main(args: Array[String]) {

		val dataFile = "file:///Users/luoli/Downloads/credit/PBC_PUBLIC_DEBIT_INFO.txt"

		val spark = SparkSession.builder().appName("TxtParseToTable").getOrCreate()
		import java.lang.Double

		import spark.implicits._

		// function to process in mapPartitionsWithIndex
		def skipLines(index: Int, lines: Iterator[String], num: Int): Iterator[String] = {
			if (index == 0) {
				lines.drop(num)
			}
			lines
		}

		val creditInfoData = 
    spark.sparkContext.textFile(dataFile)
            .mapPartitionsWithIndex((idx, iter) => skipLines(idx, iter, 1)) //  利用mapPartitionsWithIndex去除数据中第一行的schema说明行
            .filter(!_.isEmpty()).map(_.split("\\|"))
            .map( r => CreditInfo(r(0).trim.toInt,    // ID 
										   r(1),               // BUSSINESS_NO
								  		 r(2).trim.toLong,   // REPORT_CODE
								  		 r(3),               // NAME
								  		 r(4),               // DEBIT_KEY 
								  		 Double.parseDouble(r(5).trim.replaceAll(",",""))  // DEBIT_VALUE
								  		 )).toDF()

            creditInfoData.show()

    val tmpDirStr = "/tmp/creditInfo.parquet"
		/* 
		val tmpDir = new File(tmpDirStr)
		if (tmpDir.exists()) {
			try {
				tmpDir.delete()
			} catch {
				case e: Exception => println("Delete " + tmpDirStr + " FAILED!")
			}
		}
		*/

		creditInfoData.write.mode(SaveMode.Overwrite).parquet(tmpDirStr)

		val creditInfoDFLoadFromParquet =  spark.read.parquet(tmpDirStr)

		creditInfoDFLoadFromParquet.createOrReplaceTempView("credit_info")
		val selectResult = spark.sql("SELECT * FROM credit_info")
		selectResult.map( attr => "ID: " + attr(0)).show()
	}
}
