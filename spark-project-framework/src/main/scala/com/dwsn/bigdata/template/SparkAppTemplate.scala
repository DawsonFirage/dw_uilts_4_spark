package com.dwsn.bigdata.template

import com.dwsn.bigdata.utils.SparkUtils
import org.apache.spark.sql.SparkSession

object SparkAppTemplate extends App {
//  private val spark: SparkSession = SparkUtils.createSparkSession()
  private val spark: SparkSession = SparkUtils.createSparkSessionWithHiveSupport()

  // something to run...

  spark.close()
}
