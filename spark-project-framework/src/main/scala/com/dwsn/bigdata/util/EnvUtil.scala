package com.dwsn.bigdata.util

import org.apache.spark.sql.SparkSession

object EnvUtil {

  private val sparkLocal = new ThreadLocal[SparkSession]

  /**
   * 将SparkSession放入线程内存
   * @param spark SparkSession
   */
  def put(spark: SparkSession): Unit = {
    sparkLocal.set(spark)
  }

  /**
   * 从线程内存中获取SparkSession
   * @return SparkSession
   */
  def get(): SparkSession = {
    sparkLocal.get()
  }

  /**
   * 从线程内存中移除SparkSession
   */
  def clear(): Unit = {
    sparkLocal.remove()
  }

}
