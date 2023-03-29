package com.dwsn.bigdata.SQL

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkSQL02_JoinType {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[4]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

    val personDF: DataFrame = spark.read.option("header", true).csv("spark-sql/input/person/person.csv")
    val genderDF: DataFrame = spark.read.option("header", true).csv("spark-sql/input/person/gender.csv")

    personDF.createTempView("t1")
    genderDF.createTempView("t2")

    println("t1:")
    spark.sql("select * from t1").show()
    println("t2:")
    spark.sql("select * from t2").show()

//    println("INNER:")
//    spark.sql(
//      """
//        |select t1.*, t2.*
//        |from t1
//        |join t2
//        |on t1.id = t2.id
//        |""".stripMargin).show()
//
//    println("FULL [ OUTER ]:")
//    spark.sql(
//      """
//        |select t1.*, t2.*
//        |from t1
//        |full join t2
//        |on t1.id = t2.id
//        |""".stripMargin).show()
//
//    println("LEFT [ OUTER ] :")
//    spark.sql(
//      """
//        |select t1.*, t2.*
//        |from t1
//        |left join t2
//        |on t1.id = t2.id
//        |""".stripMargin).show()
//
//
//    println("RIGHT [ OUTER ] :")
//    spark.sql(
//      """
//        |select t1.*, t2.*
//        |from t1
//        |right join t2
//        |on t1.id = t2.id
//        |""".stripMargin).show()

    // SEMI链接 -> 2.x版本中left必填 3.x版本后left可省略
//    println("LEFT SEMI :")
//    spark.sql(
//      """
//        |select *
//        |from t1
//        |left semi join t2
//        |on t1.id = t2.id
//        |""".stripMargin).show()
//
//    println("ANTI :")
//    spark.sql(
//      """
//        |select *
//        |from t1
//        |anti join t2
//        |on t1.id = t2.id
//        |""".stripMargin).show()

    println("CROSS :")
    spark.sql(
      """
        |select *
        |from t1
        |cross join t2
        |""".stripMargin).show()


    spark.close()

  }

}
