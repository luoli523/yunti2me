package com.luoli523.spark

import org.apache.spark.sql.SparkSession

object HiveUsed {

  def main(args: Array[String]) {
    val ddlFile = "/Users/luoli/Downloads/trade/tAndc.txt"
    val warehouseLocation = "file:/tmp/spark-warehouse"

    def createTable(tableName: String, schema: String) : Unit = {
      val fields = schema.split("\\|")
      //spark.sql(s"CREATE TABLE IF NOT EXISTS $tableName ()")
    }

    val spark = SparkSession.builder()
                            .enableHiveSupport()
                            .appName("create hive table from tAndc schema")
                            .config("spark.sql.warehouse.dir", warehouseLocation)
                            .getOrCreate()

    val ddlRDD = spark.sparkContext.textFile(ddlFile)
    //ddlRDD.map(_.split(" ")).map( (tableName, schema) => createTable(tableName, schema) )
    // TODO
  }
}