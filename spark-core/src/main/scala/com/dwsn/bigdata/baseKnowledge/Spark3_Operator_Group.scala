package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object Spark3_Operator_Group {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 1), ("b", 1), ("b", 1), ("c", 1)))

    /*
      groupByKey : 将相同key的聚合
      重分区过程会发生shuffle，shuffle的过程伴随的数据落盘，因此shuffle会影响数据计算速度
     */
    val groupRDD: RDD[(String, Int)] = rdd.groupByKey()
      .mapValues(_.sum)
//    groupRDD.foreach(println)

    /*
      reduceByKey : 将相同key的原素聚合，但会先在shuffle前，在每个分区内预聚合
                    因此效率要高于groupByKey
     */
    val reduceRDD: RDD[(String, Int)] = rdd.reduceByKey(_ + _)
    reduceRDD.foreach(println)

    sc.stop()
  }
}
