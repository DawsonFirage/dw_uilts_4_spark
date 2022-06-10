package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object Spark01_SparkSQL_Basic {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zhangsan", 30), (2, "lisi", 20), (3, "wangwu", 40)))

    // DataFrame  <=>  RDD
    val df: DataFrame = rdd.toDF("id", "name", "age")
    val rdd1: RDD[Row] = df.rdd

    // DataSet  <=>  RDD
    val rdd2: RDD[User] = rdd.map {
      case (id, name, age) => User(id, name, age)
    }
    val ds: Dataset[User] = rdd2.toDS()
    val rdd3: RDD[User] = ds.rdd

    // DataFrame  <=> DataSet
    val ds1: Dataset[User] = df.as[User]
    val df1: DataFrame = ds.toDF()

    ds1.select('id, 'name, 'age + 3).show()

    spark.close()
  }

}

case class User (id: Int, name: String, age: Int)