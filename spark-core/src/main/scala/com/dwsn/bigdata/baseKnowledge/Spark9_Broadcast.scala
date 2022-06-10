package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark9_Broadcast {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")

    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd1: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("b", 2), ("c", 3)))
    val map: Map[String, Int] = Map(("a", 4), ("b", 5), ("c", 6))

    /*
      不使用广播变量
        由于闭包特性，闭包内使用的map会发送到每一个task中
        Executor (
          Task(map)
          Task(map)
          Task(map)
          Task(map)
        )
        会导致Executor内存重复占用
     */
    val rdd2: RDD[(String, (Int, Int))] = rdd1.map {
      case (w, c) => {
        val i: Int = map.getOrElse(w, 0)
        (w, (c, i))
      }
    }

    /*
      使用广播变量
        广播变量的数据会发送至每个Executor中，当Task需要时可以直接从Executor中访问
        Executor (
          map
          Task()
          Task()
          Task()
          Task()
        )
     */
    val bc: Broadcast[Map[String, Int]] = sc.broadcast(map)
    val rdd3: RDD[(String, (Int, Int))] = rdd1.map {
      case (w, c) => {
        val i: Int = bc.value.getOrElse(w, 0)
        (w, (c, i))
      }
    }

    rdd2.foreach(println)
    println("----------------")
    rdd3.foreach(println)


    sc.stop()
  }

}
