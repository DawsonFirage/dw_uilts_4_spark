package com.dwsn.bigdata.common

import java.util.Properties

import com.dwsn.bigdata.util.EnvUtil
import org.apache.spark.sql.{DataFrame, SparkSession}

trait TDao {

  def readTable(table: String): DataFrame = {
    val spark: SparkSession = EnvUtil.get()
    spark.sql(s"select * from ${table}")
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
