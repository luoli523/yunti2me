# 用SparkSQL处理Txt文件数据

* 场景
  * 有一批数据，用TXT文件存储，其格式如下。
  * 需要用spark对其进行split，并parse成结构化数据进行查询
  * 最后保存成parquet格式以便以后继续查询
  
```
ID|BUSSINESS_NO|REPORT_CODE|NAME|DEBIT_KEY|DEBIT_VALUE
361|8a8501c9467fe5ec01468a47adab4a31|1468330281025|2011-12|不良负债余额|0.00
362|8a8501c9467fe5ec01468a47adab4a31|1468330281025|2012-03|全部负债余额|1,210.00
363|8a8501c9467fe5ec01468a47adab4a31|1468330281025|2012-03|不良负债余额|0.00
364|8a8501c9467fe5ec01468a47adab4a31|1468330281025|2012-06|全部负债余额|1,210.00
```

代码如下：

```scala
package luoli.spark.sql

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SaveMode
import java.io.File

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
		import spark.implicits._
		import java.lang.Double

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
		creditInfoData.write.mode(SaveMode.Overwrite).parquet(tmpDirStr)

		val creditInfoDFLoadFromParquet =  spark.read.parquet(tmpDirStr)

		creditInfoDFLoadFromParquet.createOrReplaceTempView("credit_info")
		val selectResult = spark.sql("SELECT * FROM credit_info")
		selectResult.map( attr => "ID: " + attr(0)).show()
	}
}


```


```scala
package luoli.scala.sql

import org.apache.spark.sql.Row
import org.apache.spark.sql.RowFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

object TxtWithSchemaParse2Table {

	def main(args: Array[String]) {
		if (args.length < 1) {
      println("Usage: TxtWithSchemaParse2Table <txt_file_path>")
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
    import spark.implicits._

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
```