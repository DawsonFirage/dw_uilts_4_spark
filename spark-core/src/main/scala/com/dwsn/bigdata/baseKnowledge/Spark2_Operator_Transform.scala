package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark2_Operator_Transform {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[Any] = sc.makeRDD( List(1, 2, 3, 4, 5, 6, 7, 8, 9, 0) )

    // sample : 抽样 => 可以用于检查出导致数据倾斜的Key，从而对其进行优化处理
    val sampleRdd: RDD[Any] = rdd.sample(false, 0.4)
//    val sampleRdd: RDD[Any] = rdd.sample(true, 2)

    sampleRdd.foreach(println)

    sc.stop()
  }
}
