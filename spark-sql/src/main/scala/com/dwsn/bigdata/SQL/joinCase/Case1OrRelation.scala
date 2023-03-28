package com.dwsn.bigdata.SQL.joinCase

import com.dwsn.bigdata.SQL.SparkSQL02_JoinType.getClass
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object Case1OrRelation {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[4]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

    val personDF: DataFrame = spark.read.option("header", true).csv("spark-sql/input/person/person.csv")
    val courseDF: DataFrame = spark.read.option("header", true).csv("spark-sql/input/person/course.csv")

    personDF.createTempView("person")
    courseDF.createTempView("course")

    println("t1:")
    spark.sql("select * from person").show()
    println("t2:")
    spark.sql("select * from course").show()

//    val orRelationJoinDF: DataFrame = spark.sql(
//      """
//        |SELECT *
//        |FROM person
//        |    LEFT JOIN course
//        |        ON person.id = course.id
//        |        OR person.name = course.name
//        |""".stripMargin)
//
//    orRelationJoinDF.show()
//    orRelationJoinDF.explain()

    val orRelationJoinImprovedDF: DataFrame = spark.sql(
      """
        |WITH id_join AS (
        |    SELECT person.id AS pid, course.*
        |    FROM person
        |        JOIN course
        |            ON person.id = course.id
        |), name_join AS (
        |    SELECT person.id AS pid, course.*
        |    FROM person
        |        JOIN course
        |            ON person.name = course.name
        |), union_subjoin AS (
        |    SELECT * FROM id_join
        |    UNION
        |    SELECT * FROM name_join
        |)
        |SELECT *
        |FROM person
        |    LEFT JOIN union_subjoin
        |        ON person.id = union_subjoin.pid
        |ORDER BY pid
        |""".stripMargin)

    orRelationJoinImprovedDF.show()
    orRelationJoinImprovedDF.explain()

  }
}