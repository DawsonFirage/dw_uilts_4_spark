package com.dwsn.bigdata.service

import com.dwsn.bigdata.common.TService
import com.dwsn.bigdata.dao.TestDao

class TestService extends TService {

  private val dao: TestDao = new TestDao

  def dataAnalysis(): Unit = {
    dao.readTable()
  }

}
