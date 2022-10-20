package com.dwsn.bigdata.controller

import com.dwsn.bigdata.common.TController
import com.dwsn.bigdata.service.TestService

class TestController extends TController {

  private val service: TestService = new TestService

  def dispatch(): Unit = {
    service.dataAnalysis()
  }

}
