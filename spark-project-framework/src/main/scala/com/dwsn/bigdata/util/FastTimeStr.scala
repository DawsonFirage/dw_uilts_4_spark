package com.dwsn.bigdata.util

import java.util.Date

/**
 * @author Dawson
 * @date 2022-9-29 14:56:37
 * 快速获取时间字符串
 */
object FastTimeStr {

  /**
   * 获取当前日期
   * @param format 时间格式：默认 yyyy-MM-dd
   * @return
   */
  def today(format: String = DateUtil.YYYY_MM_DD): String = {
    val todayDate: Date = new Date()
    DateUtil.formatDate(todayDate, format)
  }

  /**
   * 获取当前时间
   * @param format 时间格式：默认 yyyy-MM-dd HH:mm:ss
   * @return
   */
  def currentTime(format: String = DateUtil.YYYY_MM_DD_HH_MM_SS): String = {
    today(format)
  }

  /**
   * 获取昨天的日期
   * @param format 时间格式：默认 yyyy-MM-dd
   * @return
   */
  def yesterday(format: String = DateUtil.YYYY_MM_DD): String = {
    val yesterdayDate: Date = DateUtil.getBeforeByDays(1)
    DateUtil.formatDate(yesterdayDate, format)
  }

  /**
   * 获取明天的日期
   * @param format 时间格式：默认 yyyy-MM-dd
   * @return
   */
  def tomorrow(format: String = DateUtil.YYYY_MM_DD): String = {
    val tomorrowDate: Date = DateUtil.getAfterByDays(1)
    DateUtil.formatDate(tomorrowDate, format)
  }

}
