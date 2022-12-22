package com.dwsn.bigdata.util

import java.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.spark.SparkException

object SparkCommonUdf {

  def main(args: Array[String]): Unit = {
    println(toJsonString("zhangsan", "age", 18, "sex", "M", "remark", null))
  }

  /**
   * <p>参照 hive 原生函数 name_struct() 用法，生成JSON格式字符串。</p>
   * <p>参数：属性名1, 数据1, 属性名2, 数据2, ...</p>
   * <p>注：sparkSQL udf 不直接支持动态参数，故需要传入动态参数时，需要先将其包装为Array数组。</p>
   * <p>示例：to_json_array(array('name', 'zhangsan', 'age', 18, 'sex', 'M', 'remark', null))</p>
   */
  def toJsonString(objs: Any*): String = {
    if (null == objs) return null

    // 检查参数个数是否正确
    if (0 != (objs.length % 2)) throw new SparkUdfException("参数数量错误！请确定你的参数数量无误！")

    // 将属性及数值存储在 Map 对象中
    val jsonMap: util.HashMap[String, Any] = new util.HashMap
    for (i <- 0 until (objs.length, 2)) {
      val fieldName: Any = objs(i)
      val fieldValue: Any = objs(i+1)
      // 判断属性名称类型是否正确
      if (!fieldName.isInstanceOf[String]) throw new SparkUdfException("参数类别错误！请确保你的属性名都为字符串类型！")
      jsonMap.put(fieldName.asInstanceOf[String], fieldValue)
    }

    val jsonString: String = JSON.toJSONString(jsonMap, SerializerFeature.UseISO8601DateFormat)
    if (jsonString == "{}") null else jsonString
  }

}

class SparkUdfException(message: String, cause: Throwable = null) extends SparkException(message, cause)