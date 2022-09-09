package com.dwsn.bigdata

import com.dwsn.bigdata.template.SparkAppTemplate
import org.apache.spark.sql.SparkSession

object Test {

  def main(args: Array[String]): Unit = {
    new SparkAppTemplate().template()(){
      spark: SparkSession => {
      }
    }
  }

}
