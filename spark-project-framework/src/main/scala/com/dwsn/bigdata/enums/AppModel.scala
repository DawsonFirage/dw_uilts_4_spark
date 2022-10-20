package com.dwsn.bigdata.enums

import com.dwsn.bigdata.enums

object AppModel extends Enumeration {
  type AppModel = Value

  /**
   * 测试使用。运行在本地的Spark应用
   */
  val Local: enums.AppModel.Value = Value("Local")

  /**
   * 线上使用。运行在集群的Spark应用
   */
  val Cluster = Value("Cluster")

}
