package com.dwsn.bigdata.test

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.matching.Regex

object DataCountTest3 {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("DataCountTest")
//      .set("hive.metastore.uris", "thrift://192.168.101.223:9083")
    val spark: SparkSession = SparkSession.builder.config(sparkConf).enableHiveSupport.getOrCreate

    val fileName: String = "C:/Users/hlh/Desktop/out/*/part-*"

    val dataFrame: DataFrame = spark.read.text(fileName)

    dataFrame.show(10)
    println(dataFrame.count())

    spark.close()
  }

}
