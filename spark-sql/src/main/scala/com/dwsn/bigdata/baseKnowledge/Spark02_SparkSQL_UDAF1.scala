package com.dwsn.bigdata.baseKnowledge

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, IntegerType, LongType, StructField, StructType}

object Spark02_SparkSQL_UDAF1 {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
    import spark.implicits._

    val rdd: RDD[(Int, String, Int)] = spark.sparkContext.makeRDD(List((1, "zhangsan", 30), (2, "lisi", 20), (3, "wangwu", 40)))
    val df: DataFrame = rdd.toDF("id", "name", "age")

    df.createTempView("user")

    // 注册udaf方法 => Spark 3.0版本以前
    spark.udf.register("myAvg", new MyAvgUDAF)
    spark.sql("select myAvg(age) from user").show()

    spark.close()
  }

  /**
   * Spark 定义UDAF => avg平均值
   * 1 继承 org.apache.spark.sql.expressions.UserDefinedAggregateFunction
   * 2 重写方法（8）
   * 3 注册udaf方法
   */
  class MyAvgUDAF extends UserDefinedAggregateFunction {
    // 聚合函数输入参数的数据类型
    override def inputSchema: StructType = StructType(Array(StructField("age", IntegerType)))

    // 聚合函数缓冲区中值的数据类型(age,count)
    override def bufferSchema: StructType = StructType(Array(StructField("sum", LongType), StructField("count", LongType)))

    // 函数返回值的数据类型
    override def dataType: DataType = DoubleType

    // 稳定性：对于相同的输入是否一直返回相同的输出。
    override def deterministic: Boolean = true

    // 函数缓冲区初始化
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      // 存年龄的总和
      buffer(0) = 0L
      // 存年龄的个数
      buffer(1) = 0L
    }

    // 更新缓冲区中的数据
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      if (!input.isNullAt(0)) {
        buffer(0) = buffer.getLong(0) + input.getInt(0)
        buffer(1) = buffer.getLong(1) + 1
      }
    }

    // 合并缓冲区
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1(0) = buffer1.getLong(0) + buffer2.getLong(0)
      buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
    }

    // 计算最终结果
    override def evaluate(buffer: Row): Double = buffer.getLong(0).toDouble / buffer.getLong(1)
  }

}