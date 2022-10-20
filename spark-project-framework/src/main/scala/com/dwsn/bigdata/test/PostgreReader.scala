package com.dwsn.bigdata.test

import com.dwsn.bigdata.util.SparkInitializeUtil
import org.apache.spark.sql.SparkSession

object PostgreReader {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkInitializeUtil.createSparkSession()

    spark.close()
  }

}
