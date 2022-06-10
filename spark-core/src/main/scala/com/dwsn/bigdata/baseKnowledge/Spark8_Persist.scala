package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark8_Persist {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")

    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd1: RDD[(String, Int)] = sc.textFile("spark-core/data/wordCount/word.txt")
      .flatMap(_.split(" "))
      .map(
        word => {
          println("***")
          (word, 1)
        }
      )

    val rdd2: RDD[(String, Int)] = rdd1.reduceByKey(_ + _)

    val rdd3: RDD[(String, Int)] = rdd1.reduceByKey(_ + _)

    /*
      不使用cache/persist，RDD中数据不会保留，每次action时都会执行整个血缘，可见rdd1中的逻辑随着两次action执行了两次
     */
//    rdd2.collect.foreach(println)
//    println("-----")
//    rdd3.collect.foreach(println)

    /*
      使用cache/persist，RDD的数据会进行持久化，从而可以实现数据的复用。
      但cache/persist本身不会触发作业执行，仍需要在action后才会执行。
      cache 缓存只是将数据保存起来，不切断血缘依赖。
     */
//    rdd1.cache()
//
//    rdd2.collect.foreach(println)
//    println("-----")
//    rdd3.collect.foreach(println)

    /*
      使用checkpoint，RDD的数据会进行持久化，且不会在程序执行后删除。
      checkpoint 本身不会触发作业执行，必须执行Action操作才能触发。
      如果检查点之后有节点出现问题，可以从检查点开始重做血缘，减少了开销。（能切断血缘）
      checkpoint 会独立生成job，因此如果不和 cache 同时使用，则会导致该 RDD 的操作重复执行。
     */
    sc.setCheckpointDir("spark-core/checkpoint1")

    rdd1.cache()
    rdd1.checkpoint()

    rdd2.collect.foreach(println)
    println("-----")
    rdd3.collect.foreach(println)


    sc.stop()
  }

}
