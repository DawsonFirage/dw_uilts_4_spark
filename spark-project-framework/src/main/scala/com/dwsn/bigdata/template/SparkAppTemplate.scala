package com.dwsn.bigdata.template

import com.dwsn.bigdata.utils.SparkUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class SparkAppTemplate {

  /**
   * Spark APP模板
   * @param conf SparkConf
   * @param isHiveSupport 是否开启Spark的HiveSupport
   * @param isOpenDynamicPartition 是否开启hive动态分区
   * @param run 需要执行的业务逻辑
   */
  def template(conf: SparkConf = SparkUtils.getSparkConf(SparkUtils.DEFAULT_APP_NAME))
              (isHiveSupport:Boolean = false, isOpenDynamicPartition: Boolean = false)
              (run: SparkSession => Unit): Unit = {
    val spark: SparkSession = SparkUtils.getSparkSessionWithHiveSupport()()
    import spark.implicits._

    run(spark)

    spark.close()
  }

}
