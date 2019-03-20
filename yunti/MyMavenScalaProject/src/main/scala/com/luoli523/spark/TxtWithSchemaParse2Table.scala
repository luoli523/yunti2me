package com.luoli523.spark

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._

object TxtWithSchemaParse2Table {

	def main(args: Array[String]) {
		if (args.length < 1) {
      println("Usage: TxtWithSchemaParse2Table <txt_file_path>")
      // ID|BUSSINESS_NO|REPORT_CODE|NAME|UNCREDIT_TYPE|UNCREDIT_KEY|UNCREDIT_VALUE
      // 292|8a8501c9467fe5ec01468a47adab4a31|1468330281025|贷款|正常类汇总|笔数|1
      // 293|8a8501c9467fe5ec01468a47adab4a31|1468330281025|贷款|正常类汇总|余额|3,200.00
      // 294|8a8501c9467fe5ec01468a47adab4a31|1468330281025|贸易融资|正常类汇总|笔数|0
      System.exit(1)
    }

    def dropLines (index: Int, lines: Iterator[String], linesToDrop: Int) : Iterator[String] = {
      if (index == 0) {
        lines.drop(linesToDrop)
      }
      lines
    }

    val txtFile = args(0)

    val spark = SparkSession.builder().appName("TxtWithSchemaParse2Table").getOrCreate()

    val initialRDD = spark.sparkContext.textFile(txtFile)
    val schemaString = initialRDD.take(1)(0)
    println("The Schema Line of the Table : " + schemaString)

    val fields = schemaString.split("\\|")
    val structFileds = fields.map( fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(structFileds)

    val rowRDD = initialRDD.mapPartitionsWithIndex( (index, iter) => dropLines(index, iter, 1))
                           .map(_.split('|'))
                           .map( r => Row.fromSeq(r.toSeq) )

    val dataDF = spark.createDataFrame(rowRDD, schema)
    dataDF.createOrReplaceTempView("table")

    val result = spark.sql("SELECT * FROM table")
    result.show()
	}
}