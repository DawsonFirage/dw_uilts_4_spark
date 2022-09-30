package com.dwsn.bigdata.utils

import com.dwsn.bigdata.constants.ConfConstants
import com.dwsn.bigdata.utils.hdfsUtils.HdfsSession
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @author Dawson
 */
object SparkUtils {

  /**
   * 获取基础的Spark配置文件
   * @return
   */
  def getBaseSparkConf: SparkConf = {
    val sparkConf: SparkConf = new SparkConf

    val testEnvAppName: String = s"test_${FastTimeStr.currentTime(DateUtils.YYYYMMDDHHMMSS)}"
    val testEnvSparkMaster: String = "local[*]"

    sparkConf.setAppName(sparkConf.get(ConfConstants.SPARK_APP_NAME, testEnvAppName))
    sparkConf.setMaster(sparkConf.get(ConfConstants.SPARK_MASTER, testEnvSparkMaster))

    sparkConf
  }

  /**
   * 获取Spark上下文对象
   * @param conf SparkConf
   * @return
   */
  def createSparkSession(conf: SparkConf = getBaseSparkConf): SparkSession = {
    SparkSession.builder.config(conf).getOrCreate
  }

  /**
   * 获取支持Hive的Spark上下文对象
   * @param isOpenDynamicPartition 是否开启Hive动态分区：默认不开启
   * @param conf SparkConf
   * @return
   */
  def createSparkSessionWithHiveSupport(isOpenDynamicPartition: Boolean = false,
                                        conf: SparkConf = getBaseSparkConf): SparkSession = {
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

  /**
   * 获取当前Spark运行环境下的 HDFS file system
   * @param spark
   * @return
   */
  def getHdfsConf(spark: SparkSession): Configuration = {
    spark.sparkContext.hadoopConfiguration
  }

  /**
   * 关闭SparkSession
   * @param spark
   */
  def finishJob(spark: SparkSession): Unit = {
    spark.close()
  }

}
