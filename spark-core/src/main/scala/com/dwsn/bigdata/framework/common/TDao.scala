package com.dwsn.bigdata.framework.common

import com.dwsn.bigdata.framework.util.EnvUtil
import org.apache.spark.rdd.RDD

trait TDao {

  def readFile(path: String): RDD[String] = {
    EnvUtil.take.textFile(path)
  }

}
