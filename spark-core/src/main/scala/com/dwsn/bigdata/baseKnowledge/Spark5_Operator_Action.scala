package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark5_Operator_Action {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 2), ("b", 1), ("b", 3), ("a", 3), ("c", 1)), 2)
    rdd.saveAsTextFile("spark-core/output")

    sc.stop()
  }

}
