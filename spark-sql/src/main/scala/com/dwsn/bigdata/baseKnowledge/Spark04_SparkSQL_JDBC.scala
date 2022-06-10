package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

object Spark04_SparkSQL_JDBC {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val mySqlDF: DataFrame = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://192.168.101.217:3308/yiwumysql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "dev")
      .option("password", "x1skrFBxdtFl3p4G")
      .option("dbtable", "b_project")
      .load()

    mySqlDF.limit(10).write.format("jdbc")
      .option("url", "jdbc:mysql://192.168.101.217:3308/yiwumysql")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "dev")
      .option("password", "x1skrFBxdtFl3p4G")
      .option("dbtable", "b_project_tmp")
      .mode(SaveMode.ErrorIfExists)
      .save()

    spark.close()
  }
}