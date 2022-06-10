package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark1_ReadFile {
  def main(args: Array[String]): Unit = {

    // Todo 建立与Spark框架的链接
    // 声明连接配置
    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]").setAppName(getClass.getSimpleName)
    // 获取连接的上下文对象
    val sc: SparkContext = new SparkContext(sparkConf)

    // Todo 执行业务操作
    // 本地路径
    val line: RDD[String] = sc.textFile("spark-core/data/wordCount/partition.txt", 5)
    // 文件21字节，分为五个分区
    // 21 / 5 = 4 ... 1
    // 因为 1 > 4/10 （根据hadoop分区原理，数据大于分区总数据的0.1，则创建新分区）
    // 故应有 6 个分区
    /*
      文件内容            偏移量
      1234567890@@  => 012345678901
      1234@@        => 234567
      123           => 890

      分区  偏移量        实际内容
      0 => [0,4]    => 1234567890@@
      1 => [4,8]    =>
      2 => [8,12]   => 1234@@
      3 => [12,16]  =>
      4 => [16,20]  => 123
      5 => [20,24]  =>
     */

    // hdfs
//    val line: RDD[String] = sc.textFile("hdfs://192.168.101.223:8020/user/hive/warehouse/sxf_dw.db/tmp_address_split")

    line.saveAsTextFile("spark-core/output")

//    val array: Array[(String, Int)] = line.flatMap(_.split("\\001"))
//      .map((_, 1))
//      .reduceByKey(_ + _)
//      .collect()
//
//    array.foreach(println(_))

    // Todo 关闭连接
    sc.stop()

  }
}
