package com.dwsn.bigdata.application

import com.dwsn.bigdata.common.TApplication
import com.dwsn.bigdata.enums.AppModel

object TestApplication extends App with TApplication{

  // 启动应用程序
  start(AppModel.Local){

  }

}
