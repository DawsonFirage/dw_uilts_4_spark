package com.dwsn.bigdata.framework.service

import com.dwsn.bigdata.framework.common.TService
import com.dwsn.bigdata.framework.dao.WordCountDao
import org.apache.spark.rdd.RDD

class WordCountService extends TService {

  private val wordCountDao: WordCountDao = new WordCountDao()

  def dataAnalysis(): Array[(String, Int)] = {
    val lines: RDD[String] = wordCountDao.readFile("spark-core/data/wordCount/word.txt")

    val rdd1: RDD[(String, Int)] = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)

    rdd1.collect()
  }
}
