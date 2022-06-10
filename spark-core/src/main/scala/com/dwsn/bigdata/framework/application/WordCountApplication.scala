package com.dwsn.bigdata.framework.application

import com.dwsn.bigdata.framework.common.TApplication
import com.dwsn.bigdata.framework.controller.WordCountController

object WordCountApplication extends App with TApplication {

  start() {
    val wordCountController: WordCountController = new WordCountController()
    wordCountController.execute()
  }

}
