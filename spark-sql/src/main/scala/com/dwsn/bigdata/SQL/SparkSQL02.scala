package com.dwsn.bigdata.SQL

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkSQL02 {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[4]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

    val tmpPersonCacheDf: DataFrame = spark.sql(
      """
        |SELECT
        |     id
        |    ,name
        |    ,age
        |    ,CASE
        |        WHEN (age >= 0 AND age < 50) THEN 1
        |        WHEN (age >= 50 AND age < 100) THEN 2
        |        WHEN (age >= 100 AND age < 150) THEN 3
        |        WHEN (age >= 150 AND age < 200) THEN 4
        |        WHEN (age >= 200 AND age < 250) THEN 5
        |        WHEN (age >= 250 AND age < 300) THEN 6
        |        WHEN (age >= 300 AND age < 350) THEN 7
        |        WHEN (age >= 350 AND age < 400) THEN 8
        |        WHEN (age >= 400 AND age < 450) THEN 9
        |        WHEN (age >= 450 AND age < 500) THEN 10
        |        ELSE 0
        |     END AS age_field
        |FROM tmp_person_s
        |""".stripMargin)
    tmpPersonCacheDf.cache()
    tmpPersonCacheDf.createTempView("tmp_person_cache")

    // 12个数据文件
    //    spark.sql(
    //      """
    //        |INSERT OVERWRITE LOCAL DIRECTORY "spark-sql/output/person"
    //        |ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
    //        |SELECT age_field, min(age), max(age), count(1)
    //        |FROM tmp_person_cache
    //        |GROUP BY age_field
    //        |""".stripMargin).show(500)

    println("********************************************")
    // 12个数据文件
    val planDF: DataFrame = spark.sql(
      """
        |EXPLAIN EXTENDED SELECT
        |    age_field, min(age), max(age), count(1)
        |FROM tmp_person_cache
        |GROUP BY age_field
        |""".stripMargin)
    val plan: String = planDF.collectAsList().get(0).get(0).toString
    println(plan)

    spark.close()

  }

  case class person(id: String, name: String, age: Int)

}
