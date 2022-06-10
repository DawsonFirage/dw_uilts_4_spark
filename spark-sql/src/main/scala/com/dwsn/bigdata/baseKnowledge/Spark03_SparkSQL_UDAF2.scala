package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.{Aggregator}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, SparkSession, functions}

object Spark03_SparkSQL_UDAF2 {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zhangsan", 30), (2, "lisi", 20), (3, "wangwu", 40)))
    val df: DataFrame = rdd.toDF("id", "name", "age")

    df.createTempView("user")

    // 注册udaf方法 => Spark 3.0版本以后
//    spark.udf.register("myAvg", functions.udaf(new MyAvgUDAF))
//    spark.sql("select myAvg(age) from user").show()

    spark.close()
  }

  /**
   * Spark 定义UDAF => avg平均值
   * 1 继承 org.apache.spark.sql.expressions.Aggregator
   *   定义泛型
   *     IN : 输入的数据类型
   *     BUF : 缓冲区的数据类型
   *     OUT : 输出的数据类型
   * 2 重写方法（6）
   * 3 注册udaf方法
   */
  // 定义BUF的数据类型，case class默认属性为val，因为BUF的数据需要变化，故应定义为var
  case class Buff( var total:Long, var count: Long)
  class MyAvgUDAF extends Aggregator[Long, Buff, Long] {
    // z & zero : 初始化或零值
    override def zero: Buff = {
      Buff(0L, 0L)
    }

    // 单个缓冲区聚合
    override def reduce(buff: Buff, in: Long): Buff = {
      buff.total += in
      buff.count += 1
      buff
    }

    // 缓冲区间聚合
    override def merge(buff1: Buff, buff2: Buff): Buff = {
      buff1.total += buff2.total
      buff1.count += buff2.count
      buff1
    }

    // 当缓冲区合并结束后的计算
    override def finish(buff: Buff): Long = {
      buff.total / buff.count
    }

    /*
      编码操作属于默认写法
        org.apache.spark.sql.Encoders
        自定义的类 : Encoders.product
        Scala自带的类（如Long） : Encoders.scalaLong
     */
    // 缓冲区的编码
    override def bufferEncoder: Encoder[Buff] = Encoders.product
    // 输出的编码
    override def outputEncoder: Encoder[Long] = Encoders.scalaLong
  }

}