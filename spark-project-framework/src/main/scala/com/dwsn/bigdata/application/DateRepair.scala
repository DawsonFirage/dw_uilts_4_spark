package com.dwsn.bigdata.application

import java.util.Date

import com.dwsn.bigdata.common.TApplication
import com.dwsn.bigdata.enums.AppModel
import com.dwsn.bigdata.util.{DateUtil, EnvUtil}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.util.matching.Regex

object DateRepair extends App with TApplication{

  start(AppModel.Local) {
    val spark: SparkSession = EnvUtil.get()
    import spark.implicits._

    val excelPath: String = "testData/区平台线索模板.xls"

    var structFields: List[StructField] = StructField("no", StringType) ::
      StructField("time", StringType) ::
      Nil

    val structType: StructType = StructType(structFields)

    val contentDF: DataFrame = spark.read
      .format("com.crealytics.spark.excel")
      .option("dataAddress", "'Sheet1'!A2:B1643")
      .option("header", "false")
      .option("treatEmptyValuesAsNulls", "false")
      .schema(structType)
      .load(excelPath)

    val result: Dataset[NewDataSchema] = contentDF.as[DataSchema]
      .map {
        row: DataSchema => {
          val standardizedTime: String = standardizeTime(row.time)
          NewDataSchema(row.no.toInt, row.time, standardizedTime)
        }
      }

    result.coalesce(1)
      .toDF("no", "time", "standardizedTime")
      .sort('no)
      .write
      .format("com.crealytics.spark.excel")
      .option("header", "true")
      .save("out/Worktime2.xlsx")

  }

  def standardizeTime(time: String): String = {
    val standardRegex: Regex = "^\\d{4}.\\d{1,2}.\\d{1,2}-\\d{4}.\\d{1,2}.\\d{1,2}$".r
    val onlyMonthRegex: Regex = "^(\\d{4}).(\\d{1,2})-(\\d{4}).(\\d{1,2})$".r
    val onlyStartMonthRegex: Regex = "^(\\d{4}).(\\d{1,2})$".r

    val standardTemplate: String = "yyyy.M.d"
    val onlyMonthTemplate: String = "yyyy.M"


    val standardTimeOption: Option[String] = standardRegex.findFirstIn(time)
    val onlyMonthTimeOption: Option[String] = onlyMonthRegex.findFirstIn(time)
    val onlyStartMonthTimeOption: Option[String] = onlyStartMonthRegex.findFirstIn(time)

    if (standardTimeOption.isDefined) {
      time
    } else if (onlyMonthTimeOption.isDefined) {
      val times: Array[String] = time.split("-")

      val startTime: String = times(0)
      val endTime: String = times(1)

      val startDate: Date = DateUtil.parse(startTime, onlyMonthTemplate)
      val firstDayOfMonth: Date = DateUtil.getFirstDayOfThisMonth(startDate)
      val startTimeStr: String = DateUtil.formatDate(firstDayOfMonth, standardTemplate)

      val endDate: Date = DateUtil.parse(endTime, onlyMonthTemplate)
      val lastDayOfMonth: Date = DateUtil.getLastDayOfThisMonth(endDate)
      val endTimeStr: String = DateUtil.formatDate(lastDayOfMonth, standardTemplate)

      s"${startTimeStr}-${endTimeStr}"
    } else if (onlyStartMonthTimeOption.isDefined) {
      val date: Date = DateUtil.parse(time, onlyMonthTemplate)

      val firstDayOfMonth: Date = DateUtil.getFirstDayOfThisMonth(date)
      val startTimeStr: String = DateUtil.formatDate(firstDayOfMonth, standardTemplate)

      val lastDayOfMonth: Date = DateUtil.getLastDayOfThisMonth(date)
      val endTimeStr: String = DateUtil.formatDate(lastDayOfMonth, standardTemplate)

      s"${startTimeStr}-${endTimeStr}"
    } else {
      null
    }
  }

  case class DataSchema(no: String, time: String)
  case class NewDataSchema(no: Int, time: String, standardizedTime: String)

}
