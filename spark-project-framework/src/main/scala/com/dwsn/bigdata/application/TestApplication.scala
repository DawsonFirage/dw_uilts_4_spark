package com.dwsn.bigdata.application

import com.dwsn.bigdata.common.TApplication
import com.dwsn.bigdata.controller.TestController

object TestApplication extends App with TApplication{

  // 启动应用程序
  start(){
    val controller: TestController = new TestController()
    controller
  }

}
