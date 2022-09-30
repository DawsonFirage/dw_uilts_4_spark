package com.dwsn.bigdata.utils

import com.dwsn.bigdata.constants.ConfConstants
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.Properties

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

  /**
   * 设置jdbc连接的配置文件
   * @param driver
   * @param user
   * @param password
   * @return
   */
  private def getDBProp(driver: String, user: String, password: String): Properties = {
    val prop = new Properties()
    prop.put("driver", driver)
    prop.put("user", user)
    prop.put("password", password)
    prop
  }

  /**
   * jdbcReader
   * @param spark sparkSession
   * @param dbUrl 数据库链接
   * @param tableName 查询数据的表名
   * @param prop 连接数据库其他参数 -> getDBProp()
   * @return
   */
  def jdbcReader(spark: SparkSession,
                 dbUrl: String,
                 tableName: String,
                 prop: Properties): DataFrame = {
    spark.read.jdbc(dbUrl, tableName, prop)
  }

  def postgreReader(spark: SparkSession,
                    dbUrl: String,
                    tableName: String,
                    user: String,
                    password: String): DataFrame = {
    val prop: Properties = getDBProp("org.postgresql.Driver", user, password)
    jdbcReader(spark, dbUrl, tableName, prop)
  }

  def mysqlReader(spark: SparkSession,
                  dbUrl: String,
                  tableName: String,
                  user: String,
                  password: String): DataFrame = {
    val prop: Properties = getDBProp("com.mysql.jdbc.Driver", user, password)
    jdbcReader(spark, dbUrl, tableName, prop)
  }

  /**
   * jdbcWriter
   * @param df 待写入数据的 DataFrame
   * @param dbUrl 数据库链接
   * @param tableName 查询数据的表名
   * @param prop 连接数据库其他参数 -> getDBProp()
   * @param overwrite 是否覆盖原有数据
   */
  def jdbcWriter(df: DataFrame,
                 dbUrl: String,
                 tableName: String,
                 prop: Properties,
                 overwrite: Boolean = false) = {
    var writeMode: String = null
    if (overwrite) {
      writeMode = "overwrite"
    } else {
      writeMode = "append"
    }
    df.write.mode(writeMode)
      .jdbc(dbUrl, tableName,prop)
  }

}
