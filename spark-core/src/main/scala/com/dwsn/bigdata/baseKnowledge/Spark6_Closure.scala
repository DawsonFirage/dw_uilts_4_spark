package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark6_Closure {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建 SparkContext，该对象是提交 Spark App 的入口
    val sc: SparkContext = new SparkContext(sparkConf)

    //3.创建一个 RDD
    val rdd: RDD[String] = sc.makeRDD(Array("hello world", "hello spark", "hive", "atguigu"))

    //3.1 创建一个 Search 对象
    val search = new Search("hello")

    /*
      Spark程序中，所有Rdd的算子操作会分发至Executor端执行，而所有非RDD的算子操作会在Driver端执行
     */

    //3.2 函数传递，打印：ERROR Task not serializable
//    search.getMatch1(rdd).collect().foreach(println)

    //3.3 属性传递，打印：ERROR Task not serializable
    search.getMatch2(rdd).collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }

  class Search(query: String) {

    def isMatch(s: String): Boolean = {
      s.contains(query)
    }

    // 函数序列化案例
    def getMatch1(rdd: RDD[String]): RDD[String] = {
      rdd.filter(isMatch)
      /*
        出错原因：因为 filter 属于算子操作，其调用方法 isMatch() 中会使用 Search类 的 属性query
        因此需要对 Search类 进行序列化
       */
    }

    // 属性序列化案例
    def getMatch2(rdd: RDD[String]): RDD[String] = {
      rdd.filter(x => x.contains(query))
      /*
        出错原因：因为 filter 属于算子操作，其直接使用了 Search类 的 属性query
        因此需要对 Search类 进行序列化
       */

      //val q = query
      //rdd.filter(x => x.contains(q))
      /*
        此处传入算子内部的数据不再是 类的属性，而是一个 String类型 的 参数q
        且String类型已经序列化，故不需要对 Search类 进行序列化
       */
    }
  }

}