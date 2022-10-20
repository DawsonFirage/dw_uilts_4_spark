package com.dwsn.bigdata.common

import com.dwsn.bigdata.enums.AppModel
import com.dwsn.bigdata.enums.AppModel.AppModel
import com.dwsn.bigdata.util.{EnvUtil, SparkInitializeUtil}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait TApplication {

  /**
   * SparkApp运行框架
   * @param model SparkApp运行模式。默认Cluster
   * @param hiveSupport 是否开启Hive Support。默认开启
   * @param op 操作代码
   */
  def start(model: AppModel = AppModel.Cluster, hiveSupport: Boolean= true)(op: => Unit): Unit = {

    val conf: SparkConf = model match {
      case AppModel.Local => new SparkConf().setAppName(getClass.getSimpleName.replace("$", "")).setMaster("local[*]")
      case AppModel.Cluster => new SparkConf()
    }

    val spark: SparkSession = if (hiveSupport) {
      SparkInitializeUtil.createSparkSessionWithHiveSupport(conf)
    } else {
      SparkInitializeUtil.createSparkSession(conf)
    }

    EnvUtil.put(spark)

    try {
      op
    } catch {
      case ex: Exception => println(ex.getMessage)
    }

    spark.close()
    EnvUtil.clear()

  }

}
