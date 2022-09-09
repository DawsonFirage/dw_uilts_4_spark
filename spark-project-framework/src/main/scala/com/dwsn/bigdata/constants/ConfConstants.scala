package com.dwsn.bigdata.constants

object ConfConstants {
  // Hadoop Conf
  val HADOOP_USER_NAME: String = "HADOOP_USER_NAME"
  val HADOOP_USER_NAME_DEFAULT_VALUE: String = "admin"
  val MR_FILE_OUTPUT_SUCCESS_MARK: String = "mapreduce.fileoutputcommitter.marksuccessfuljobs"

  // Hive Conf
  val HIVE_METASTORE_URIS: String = "hive.metastore.uris"
  val HIVE_EXEC_DYNAMIC_PARTITION: String = "hive.exec.dynamic.partition"
  val HIVE_EXEC_DYNAMIC_PARTITION_MODE: String = "hive.exec.dynamic.partition.mode"
}
