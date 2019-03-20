package com.luoli523.scala

import scala.io.Source

object ProcessDDLFile {
  
  def main(args: Array[String]) {
    val ddlFile = "/Users/luoli/Downloads/trade/ddlV3.txt"

    var unfinishedLine = ""
    
    val wholeLinePattern = "^\".*\"$".r
    val checkPattern = "[^\"]$".r
    val tAndcPattern = "^\"(.*?)\"\\s+\"(.*?)\"\\s+\"(.*?)\".*$".r

    var previousTableName = ""
    var columnString = ""

    for (line <- Source.fromFile(ddlFile).getLines) {
      line match {
        /*
        // The process Logic of parse the line
        case wholeLinePattern(_*) => println(line)
        case checkPattern(_*) => { 
          unfinishedLine += line
          println(unfinishedLine)
          unfinishedLine = ""
        }
        case _ => unfinishedLine += line
        */
        case tAndcPattern(tableName, _, columnName) => {
          if (previousTableName == tableName) {
            if (!columnString.isEmpty) {
              columnString +=  "|" + columnName
            }
          } else {
            if (!previousTableName.isEmpty()) {
              println(s"$previousTableName $columnString")               
            }
            previousTableName = tableName
            columnString = columnName
          }
        }
        case _ => 
      }
    }
  } 
}