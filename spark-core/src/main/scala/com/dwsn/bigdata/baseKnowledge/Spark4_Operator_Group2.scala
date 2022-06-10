package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Spark4_Operator_Group2 {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setAppName(getClass.getSimpleName)
      .setMaster("local[*]")
    val sc: SparkContext = new SparkContext(sparkConf)

    val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a", 1), ("a", 2), ("b", 2), ("b", 1), ("b", 3), ("a", 3), ("c", 1)), 2)
    rdd.saveAsTextFile("spark-core/output")

    /*
      aggregateByKey : 可以分别定义shuffle之前和之后的聚合规则
                       zeroValue 每个Key的聚合初始值
     */
    // 在分区内取最大值，在分区外求和
    val aggRDD: RDD[(String, Int)] = rdd.aggregateByKey(0)(
      (x, y) => math.max(x, y),
      (x, y) => x + y
    )
//    aggRDD.foreach(println)

    /*
      foldByKey : 聚合前后功能相同版的aggregateByKey，有初始值的reduceByKey
     */
    val foldRDD: RDD[(String, Int)] = rdd.foldByKey(0)(_+_)
//    foldRDD.foreach(println)

    /*
      combineByKey : 无初始值版的aggregateByKey，但同样允许改变第一个聚合的原素的类型
     */
    val combineRDD: RDD[(String, Int)] = rdd.combineByKey(
      v=>v,
      (x, y) => math.max(x, y),
      (x, y) => x + y)
//    combineRDD.foreach(println)

    /**
     * reduceByKey/aggregateByKey/foldByKey/combineByKey
     * 底层实现均为
     *  combineByKeyWithClassTag(
     *    `createCombiner`, which turns a V into a C (e.g., creates a one-element list)
     *    `mergeValue`, to merge a V into a C (e.g., adds it to the end of a list)
     *    `mergeCombiners`, to combine two C's into a single one.
     *    ...
     *  )
     *
     * reduceByKey
     *   combineByKeyWithClassTag[V](
     *     (v: V) => v,        // 初始值不变
     *     func,               // 分区内计算规则（same）
     *     func,               // 分区间计算规则（same）
     *     partitioner
     *   )
     *
     * aggregateByKey
     *   combineByKeyWithClassTag[U](
     *     (v: V) => cleanedSeqOp(createZero(), v),  // 初始值计算
     *     cleanedSeqOp,                             // 分区内计算规则
     *     combOp,                                   // 分区间计算规则
     *     partitioner
     *   )
     *
     * foldByKey
     *   combineByKeyWithClassTag[V](
     *     (v: V) => cleanedFunc(createZero(), v),  // 初始值计算
     *     cleanedFunc,                             // 分区内计算规则（same）
     *     cleanedFunc,                             // 分区间计算规则（same）
     *     partitioner
     *   )
     *
     * combineByKey
     *   combineByKeyWithClassTag(
     *     createCombiner,
     *     mergeValue,
     *     mergeCombiners,
     *     defaultPartitioner(self)
     *   )
     */

    sc.stop()
  }
}
