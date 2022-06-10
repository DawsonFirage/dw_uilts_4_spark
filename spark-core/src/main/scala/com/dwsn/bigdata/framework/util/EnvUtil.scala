package com.dwsn.bigdata.framework.util

import org.apache.spark.SparkContext

object EnvUtil {

  private val scLocal: ThreadLocal[SparkContext] = new ThreadLocal[SparkContext]()

  def put(sc: SparkContext): Unit = {
    scLocal.set(sc)
  }

  def take(): SparkContext = {
    scLocal.get()
  }

  def clear(): Unit = {
    scLocal.remove()
  }
}
