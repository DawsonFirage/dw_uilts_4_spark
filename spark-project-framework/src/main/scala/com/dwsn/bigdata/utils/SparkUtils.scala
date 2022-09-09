package com.dwsn.bigdata.utils

import java.util.Date

import com.dwsn.bigdata.constants.ConfConstants
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author Dawson
 */
object SparkUtils {

  /**
   * 本地测试时使用默认名称
   */
  val DEFAULT_APP_NAME: String = s"localTest_${DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS)}"

  /**
   * 获取Spark配置文件
   * @param appName appName
   * @return
   */
  def getSparkConf(appName: String): SparkConf = {
    val sparkConf: SparkConf = new SparkConf()

    // 测试时指定 AppName 和 Master
    if ( null == sparkConf.get("spark.app.name") ) sparkConf.setAppName(appName)
    if ( null == sparkConf.get("spark.master") ) sparkConf.setMaster("local[*]")

    sparkConf
  }

  /**
   * 获取Spark上下文对象
   * @param conf SparkConf
   * @return
   */
  def getSparkSession(conf: SparkConf = getSparkConf(DEFAULT_APP_NAME)): SparkSession = {
    SparkSession.builder.config(conf).getOrCreate
  }

  /**
   * 获取支持Hive的Spark上下文对象
   * @param conf SparkConf
   * @param isOpenDynamicPartition 是否开启Hive动态分区
   * @return
   */
  def getSparkSessionWithHiveSupport(conf: SparkConf = getSparkConf(DEFAULT_APP_NAME))
                                    (isOpenDynamicPartition: Boolean = false): SparkSession = {
    // 设置HADOOP用户，方便通过Hive向HDFS写入数据
    System.setProperty(ConfConstants.HADOOP_USER_NAME, ConfConstants.HADOOP_USER_NAME_DEFAULT_VALUE)
    if (isOpenDynamicPartition) {
      // 向 Hive 插入数据时开启动态分区
      conf.set(ConfConstants.HIVE_EXEC_DYNAMIC_PARTITION, "true")
      conf.set(ConfConstants.HIVE_EXEC_DYNAMIC_PARTITION_MODE, "nonstrict")
    }

    val spark: SparkSession = SparkSession.builder.config(conf)
      .enableHiveSupport
      .getOrCreate

    // 不在HDFS产生_success这个成功标记位文件
    spark.sparkContext.hadoopConfiguration.set(ConfConstants.MR_FILE_OUTPUT_SUCCESS_MARK, "false")
    spark
  }



}
