package com.luoli523.spark

import org.apache.spark.sql.SparkSession

object ParseDDL {

  def main(args: Array[String]) {
    if (args.length < 1) {
      println("Usage: ParseDDL <ddl_file_path>")
      System.exit(1)
    }

    def dropLines (index: Int, lines: Iterator[String], linesToDrop: Int) : Iterator[String] = {
      if (index == 0) {
        lines.drop(linesToDrop)
      }
      lines
    }

    def extractFunction (line : String) : Array[String] = {
      val p = "\"(\\w+)\"\\s+\"(\\w*)\"\\s+\"(\\w+)\"\\s+\"(\\w+)\"\\s+\"([0-9]+)\"".r
      val p(tableName, tableComment, columnName, columnCommnet, columnIndex) = line
      Array(tableName, tableComment, columnName, columnCommnet, columnIndex)
    }

    // The ddl file format like below:
    // "TABLE_NAME"  "TABLE_COMMENT" "COLUMN_NAME" "COLUMN_COMMENT"  "ORDINAL_POSITION"
    // "i_acct_list" ""  "brchno"  "帐户部门"  "1"
    // "i_acct_list" ""  "subsac"  "子户号"  "2"
    // "i_cbk_ibs_jjjy"  "POS交易过渡表" "trandt"  "交易日期"  "1"
    // "i_cbk_ibs_jjjy"  "POS交易过渡表" "acctdt"  "报表日期"  "2"
    // "i_cbk_ibs_jjjy"  "POS交易过渡表" "transq"  "交易流水"  "3"
    val ddlFile = args(0)

    val spark = SparkSession.builder().appName("ParseDDL").getOrCreate()

    val ddl = spark.sparkContext.textFile(ddlFile)

    // parse the raw schema line and product (tablename => (columnName, colIndex))
    val table2columnRDD = ddl.map(_.replaceAll("\"\"","null"))
                             .map(_.replaceAll("\"",""))
                             .map(_.split("\\s+"))
                             .map( array => (array(0), (array(2), array(4))))
  }
}