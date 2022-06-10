package com.dwsn.bigdata.framework.controller

import com.dwsn.bigdata.framework.common.TController
import com.dwsn.bigdata.framework.service.WordCountService

class WordCountController extends TController {

  private val wordCountService: WordCountService = new WordCountService()

  def execute() = {
    val array: Array[(String, Int)] = wordCountService.dataAnalysis()
    array.foreach(println)
  }

}
