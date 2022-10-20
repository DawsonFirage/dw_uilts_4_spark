package com.dwsn.bigdata.dataManipulationContext

import java.util.Properties

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Spark JDBC Reader
 *
 * @param dbUrl 连接数据库的URL
 * @param driver 数据库驱动Class名称
 * @param user 用户名
 * @param password 密码
 */
class JdbcContext(
                   private val spark: SparkSession,
                   private val dbUrl: String,
                   private var driver: String,
                   private var user: String,
                   private var password: String
                ){

  /**
   * 通过 Jdbc
 *
   * @param tableName
   */
  def read(tableName: String): DataFrame = {
    val prop = new Properties()
    prop.put("driver", driver)
    prop.put("user", user)
    prop.put("password", password)
    spark.read.jdbc(dbUrl, tableName, prop)
  }

  def write(): Unit = {

  }

}
