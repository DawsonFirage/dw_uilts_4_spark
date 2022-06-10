package com.dwsn.bigdata.test

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.matching.Regex

object DataCountTest2 {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("DataCountTest")
      .set("hive.metastore.uris", "thrift://192.168.101.223:9083")
    val spark: SparkSession = SparkSession.builder.config(sparkConf).enableHiveSupport.getOrCreate

    val fileName: String = "2020年金东区网信总量"
    val inFileNameStr: String = s"testData/${fileName}"
    val outFileDocStr: String = s"testData/out/${fileName}-未匹配"

    val dataYearStr: String = yearAnalysis(fileName)

    val testDataDF: DataFrame = spark.read.text(inFileNameStr)
    testDataDF.cache()

    println(s"${inFileNameStr} Excel数据：${testDataDF.count}条")
    testDataDF.createTempView("tmp_spark_test_data")

    spark.sql("USE jdxfj_dw")
    val dbDataDF: DataFrame = spark.sql(
      s"""
         |SELECT outid
         |FROM dm_work_order_info_dd_i
         |WHERE event_source = '浙江省统一投诉咨询举报平台'
         |  AND (data_source = '网' OR data_source = '网')
         |  AND reporting_time LIKE '${dataYearStr}%'
         |""".stripMargin)
    dbDataDF.cache()

    println(s"DB数据：${dbDataDF.count}条")
    dbDataDF.createTempView("tmp_spark_db_data")

    val resultDF: DataFrame = spark.sql(
      """
        |SELECT a.value
        |FROM tmp_spark_test_data AS a
        |    FULL OUTER JOIN tmp_spark_db_data AS b
        |        ON a.value = b.outid
        |WHERE b.outid IS NULL
        |""".stripMargin)
    resultDF.cache()

    println("未匹配数据：")
    resultDF.show(10)
    resultDF.repartition(1).write.csv(outFileDocStr)
    println(s"共 ${resultDF.count}条")

    spark.close()
  }

  def yearAnalysis(fileName: String) = {
    val pattern: Regex = "^([0-9]*)年".r
    pattern.findFirstMatchIn(fileName).get.group(1)
  }

}
