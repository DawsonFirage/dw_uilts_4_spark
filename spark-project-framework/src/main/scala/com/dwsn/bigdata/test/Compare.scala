package com.dwsn.bigdata.test

import com.dwsn.bigdata.util.SparkInitializeUtil
import org.apache.spark.sql.{DataFrame, SparkSession}

object Compare {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkInitializeUtil.createSparkSession()
    import spark.implicits._

    val tableName: String = "tableName"
    // 数据字典
    val dicList: DataFrame = List(
      "义乌辖内上市挂牌企业名单表", "省级隐形冠军表", "省级隐形冠军培育企业表", "国家单项冠军产品企业表", "国家单项冠军培育企业表", "国家专精特新小巨人表", "国家高新技术企业清单表", "项目基本信息表", "用户部门关联表", "部门表", "尽职调查表", "用户表", "项目决策表", "部门联审表", "会议决策表", "项目推送表", "项目签约表", "字典项表", "申请表单", "申请表单详细", "政策分类表", "问题填报", "未知", "未知", "评价", "交办单", "交办单子表", "反馈表", "亩产效益评价结果表"
    ).toDF(tableName)

    // 产品给的
    val proList: DataFrame = List(
      "企业风险信号清单信息", "在建工地信息", "拖欠工资黑名单信息", "环境违法失信黑名单信息", "国家局黑名单信息", "严重违法失信企业信息（国家）", "股东（或发起人）或投资人信息（新）", "建设项目环境影响报告书审批意见信息", "在线平台-投资项目基本信息", "在线平台-投资项目调度信息", "义乌市企业年度亩效益综合评价信息", "义乌市国家专精特新“小巨人”信息", "义乌市省级隐形冠军企业名单信息", "义乌市省级隐形冠军培育企业信息", "义乌市国家单项冠军培育企业信息", "义乌市亩产效益综合评价结果信息", "义乌国家单项冠军产品企业信息", "高新技术企业信息", "金华市市场监督管理局智慧监管系统-企业风险点信息", "项目基本信息表", "用户部门关联表", "部门表", "尽职调查表", "用户表", "项目决策表", "部门联审表", "会议决策表", "项目推送表", "项目签约表", "字典项表", "申请表单", "申请表单详细", "政策分类表", "问题填报", "办理结果", "业务实例", "评价", "交办单", "反馈表", "交办单子表"
    ).toDF(tableName)

    println(s"数据字典的表有 ${dicList.count()} 条。")
    println(s"产品给的的表有 ${proList.count()} 条。")

    dicList.createTempView("dicList")
    proList.createTempView("proList")

    spark.sql(
      """
        |select dicList.tableName, proList.tableName
        |from dicList
        |    full join proList
        |    on dicList.tableName = proList.tableName
        |where dicList.tableName is null
        |   or proList.tableName is null
        |order by dicList.tableName, proList.tableName
        |""".stripMargin).show(100)

    spark.close()
  }
}
