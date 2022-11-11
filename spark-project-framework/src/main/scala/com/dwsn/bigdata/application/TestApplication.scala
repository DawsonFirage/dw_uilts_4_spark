package com.dwsn.bigdata.application

import com.dawson.ansj.udf.Udf
import com.dwsn.bigdata.common.TApplication
import com.dwsn.bigdata.enums.AppModel
import com.dwsn.bigdata.util.EnvUtil
import org.apache.spark.sql.{DataFrame, SparkSession}

object TestApplication extends App with TApplication{

  // 启动应用程序
  start(AppModel.Local){
    val spark: SparkSession = EnvUtil.get()
    import spark.implicits._

    spark.sql("use sxf_dw")

    spark.udf.register("analysis", Udf.analysis _)

    val wordListDF: DataFrame = spark.sql(
      """
        |SELECT
        |     id
        |    ,concat_ws('-', event_type, first_class, second_class, third_class) AS catalog
        |    ,ex.word
        |FROM dm_work_order_info_dd_i
        |    LATERAL VIEW explode(split(analysis(description), ',')) ex AS word
        |WHERE description IS NOT NULL AND description != ''
        |""".stripMargin)

//    wordListDF.sort("word").show(1000)
    wordListDF.createTempView("word_list")

    val resultDF: DataFrame = spark.sql(
      """
        |SELECT
        |     catalog
        |    ,word
        |    ,count(DISTINCT id) AS event_cnt
        |    ,count(1) AS word_cnt
        |FROM word_list
        |GROUP BY catalog, word
        |""".stripMargin)

    resultDF.coalesce(1).sort('event_cnt.desc, 'word_cnt.desc).write.format("csv").save("spark-project-framework/out/")

  }

}
