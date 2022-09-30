package com.dwsn.bigdata.constants

object ConfConstants {
  /**
   * Spark conf name & default value
   */
  val SPARK_APP_NAME = "spark.app.name"
  val SPARK_MASTER = "spark.master"

  /**
   * Hadoop conf name & default value
   */
  val HADOOP_USER_NAME: String = "HADOOP_USER_NAME"
  val HADOOP_USER_NAME_DEFAULT_VALUE: String = "admin"
  val MR_FILE_OUTPUT_SUCCESS_MARK: String = "mapreduce.fileoutputcommitter.marksuccessfuljobs"
  val HDFS_DEFAULT_FS: String = "fs.defaultFS"
  val HDFS_DEFAULT_FS_DEFAULT_VALUE: String = "hdfs://node001:9000"

  /**
   * Hive conf name & default value
   */
  val HIVE_METASTORE_URIS: String = "hive.metastore.uris"
  val HIVE_EXEC_DYNAMIC_PARTITION: String = "hive.exec.dynamic.partition"
  val HIVE_EXEC_DYNAMIC_PARTITION_MODE: String = "hive.exec.dynamic.partition.mode"

  /**
   * DW conf
    */
}
