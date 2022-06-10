package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark7_Serializable {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")
      // 替换默认的序列化机制
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      // 注册需要使用Kryo序列化的自定义类
      .registerKryoClasses(Array(classOf[Search3], classOf[Search4]))

    val sc: SparkContext = new SparkContext(sparkConf)

    /*
      code...
     */

    sc.stop()
  }

  // Java (自带的序列化) : 信息全，生产的数据体量大，网络传输慢
  class Search(query: String) extends Serializable
  case class Search2(query: String)

  // Kryo (Spark2.0开始支持的序列化) : 数据体量更小，序列化时可以绕过Java的transient关键字，拿到完整的数据。
  class Search3(query: String) extends Serializable
  case class Search4(query: String)

}
