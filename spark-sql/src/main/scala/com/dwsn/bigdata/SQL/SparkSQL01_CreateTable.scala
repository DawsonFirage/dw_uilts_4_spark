package com.dwsn.bigdata.SQL

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * SparkSQL docs: https://spark.apache.org/docs/latest/sql-programming-guide.html
 */
object SparkSQL01_CreateTable {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[4]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

//    spark.sql(
//      """
//        |CREATE TABLE IF NOT EXISTS tmp_person_s (
//        |    id VARCHAR(32),
//        |    name VARCHAR(32),
//        |    age INT
//        |)
//        |ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
//        |STORED AS TEXTFILE
//        |""".stripMargin)
//    spark.sql("LOAD DATA LOCAL INPATH 'spark-sql/input/person' OVERWRITE INTO TABLE tmp_person_s")

    spark.sql(
      """
        |CREATE EXTERNAL TABLE IF NOT EXISTS tmp_person_s (
        |    id VARCHAR(32),
        |    name VARCHAR(32),
        |    age INT
        |)
        |ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
        |STORED AS TEXTFILE
        |LOCATION 'spark-sql/input/person'
        |""".stripMargin)

    spark.sql("SELECT * FROM tmp_person_s").show(500)

  }

}
