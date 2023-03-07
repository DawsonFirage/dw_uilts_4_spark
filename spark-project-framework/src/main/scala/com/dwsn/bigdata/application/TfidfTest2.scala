package com.dwsn.bigdata.application

import com.dawson.ansj.udf.Udf
import com.dwsn.bigdata.common.TApplication
import com.dwsn.bigdata.enums.AppModel
import com.dwsn.bigdata.util.EnvUtil
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel, IDF}
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.mutable

object TfidfTest2 extends App with TApplication {

  start(AppModel.Local) {
    val spark: SparkSession = EnvUtil.get()
    import spark.implicits._

    val contentList: List[String] = "其是网易（杭州）网络有限公司（滨江区长河街道网商路599号4幢7层）旗下游戏梦幻西游手游的用户，游戏ID： 193621654，服务器：春华秋月 账号：834731174，反映现该公司以其曾售卖账号为由将账号隔离60天并冻结金币，向该公司索要出售证据无法提供，故来电投诉，要求恢复账号，请相关部门帮助处理。      此件曾交办：滨江区，反馈：一、调查核实过程和结果:2021年12月15日联系反映人获取反映的相关信息。 二、处理意见和时限:根据反映人所提供的信息，滨江区新闻出版局已将该问题信息告知游戏公司进行协商，游戏公司表示待有最终结果后会联系反映人告知其结果，请反映人耐心等待。 三、回访情况：2021年12月15日14时36分，杭州市滨江区新闻出版局工作人员与反映人联系，告知反映人相关情况，反映人表示知晓。      现来电反映该游戏公司，至今未联系其本人，来电投诉，要求告知处理结果，请相关部门帮助处理。   （原编号：电0054202112080078 系第二次交办）" ::
      "（BM）【网站得知】曾来电反映：余杭区仓前街道爱橙街与思凯路交叉口处，以及思凯路与永兴路的交叉口处，均有工地进行夜间施工，噪音已经影响居民正常休息，该情况已经持续两天，其希望对工地噪音进行检测并在规定的时间内进行施工，望相关部门帮助处理。 此件曾交办余杭区未来科技城（海创园）管委会，反馈：您于2021年12月28日向余杭区信访局反映了爱橙街与思凯路交叉口处，以及思凯路与永兴路的交叉口处，均有工地进行夜间施工的情况，经科技城城管中队核实处理现予以回复：接信访后，队员曾宪飞于12月30日22:17巡查至字节跳动工地，现场已停工，22:28到达钉钉工地，现场已停工。后续中队将加强对在建工地的管理，减少夜间施工现象的发生。由于信访人信息保密，无法回复。建议举报人通过中队24小时值班电话89056794联系，中队可以及时处置。 现来电反映：依旧有施工现象，希望相关部门可以继续加强管理，请相关部门核实处理。 （原编号：电A8025202112291116，系二次交办）" ::
      "徐家村油麻仓卢志红投诉内容：诉求：1，请求中央马上兑现承诺落实上海市委书记。2，解决我的人身安全生命安全。3，解决卢志红浙江省委书记或者省长最好省委书记。4，撤销我的假精神病鉴定。5，我的事情不要叫龙游县詹家镇政府管我，他们不讲诚信。事实和理由：2021年3月31号国家信访局领导答应我一个月落实上海市委书记的。现在都10月份了超了6个多月了。我不能等我跟国家信访局领导说过不能等一等就要被害死的，结果真的被害。5月28号我都不登记为他们着想不让他们扣分还没事找事残害我。9月1号我到省信访局问我工作的事情上海市委书记什么时候落实？我还顺便提了一下我丈夫卢志红的工作。我说卢志红做厨师长做管理下面也有事情也有矛盾问题纠纷的，卢志红都能解决问题。卢志红做了二十多年管理为人民服务从来没有害过人，没有做过伤天害理的事。那为什么省委书记袁家军也是做管理的为人民服务就不能解决问题害人残害我呢？为人民服务服务什么呀？人都要被他害死了。没有他服务我们还过的好好的还不会被害。他就是唐朝卢杞嫉贤妒能不得人心。信访人黄根香恨袁家军是吃人灭人灭党的毒瘤。这样一对比那卢志红比袁家军好，比他更适合当省委书记。省信访局干部也认可赞同卢志红比袁家军好，还有衢州市信访局领导干部也认可赞同。金华市驻京办领导还说我丈夫卢志红很有智慧能解决问题。我举贤不避亲，能者上、庸者下、劣者汰。我把我救人、抓小偷、义务劝访等给省信访局干部看，她说我做了伟大的事业。她还叫我教她们怎样劝访做群众工作。她说我是优秀人才浙江省都找不出一个善于做群众工作的人才。她还说我七一心得体会写的那么好可以写书。还有9月1号我去省公安厅。省公安厅领导很好答应我上海市委书记的，并且说把我假精神病鉴定撤销。我把我救人、抓小偷、义务劝访的先进事迹给领导看了。领导说我思想觉悟这么高的还义务劝访！领导还说叫我别去北京他说帮我解决上海市委书记说很快的，转下去交龙游或者衢江办。看在我表现好七一八一讲诚信都没有去北京省里。救救我和我的家人。我一天都等不了了我们全家人都没有工作、没有生活来源生活非常的困难难生存了。七一，习总书记说：解决人民群众急难愁盼。我答应省驻京办十月一号不去北京我讲诚信，他叫我不用去北京叫我去省里找他。我的思想像习总书记、实干像焦裕禄、忠诚像诸葛亮、担当像雷锋、信义像尾生、做群众工作像毛主席、识人用人像曾国藩、办案像唐朝宰相狄仁杰、口才像著名律师、像诸葛亮。我是德才兼备特殊人才急救我！事发地：北京市市辖区丰台区浙江省衢州市龙游县詹家镇油麻仓投诉人手机号：15355251579" ::
      "其是临平区崇贤街道源翠府（临平区崇贤第一小学陆家桥校区东侧）8幢1404室的业主，房屋于2021年10月1日精装修交付，现来电反映2个问题：1.房屋交付时未发现问题，但现进行二次装修时，发现客厅、餐厅及卧室天花板的混凝土结构墙存在大面积的裂缝，向开发商反映后未给予反馈，故来电求助，要求检测房屋安全并给予处理方案；2.原先开发商宣传小区大门入口处有水池喷泉，但现并未设置该水景，要求根据宣传整改，请相关部门帮助处理。      此件曾交办临平区，反馈：正在处理中。      现来电表示已接到市场监管局的反馈电话，表示无法处理此事，要求对应监管部门给予答复，并表示该情况多次向临平区质监站投诉无果，请相关部门帮助处理。      （原编号：电0005202112270185  系第二次交办）  " ::
      "信访人（姓名：王加平，身份证号：53262519730901）于2021年8月开始在场口镇第四人民医院正门对面中铁二十五局工作，从事木工工作。负责人姓名：戚光文，电话：18658886096，已签订劳动合同。已于2022年1月1日办理解除(终止)劳动合同手续，截至来电时间已超过5日，现该公司拖欠信访人工资11870元，拖欠时间为2021年8月-2022年1月,现恳请相关部门帮助督促该公司尽快发放薪资。 （双方约定2021年12月底发工资）" ::
      "我是翡翠城灵峰苑6幢居民，地铁3号线高教路站施工对灵峰苑6、7幢产生严重影响，包括但不限于：1、地铁基坑开挖造成绝大部分居民家中墙体、楼梯间墙体产生大量裂缝；2、地铁施工造成地下室地面、楼板瓷砖多处开裂；以及居民家中天花板开裂；3、施工震动造成两幢楼隔音效果严重下降。经过多次反馈，地铁施工方腾达建设项目负责人承诺施工影响期结束会协商解决，但是居民的忍让和对杭州地铁建设的支持，换来的却是地铁方的故意拖延和欺骗，目前高教路站施工已接近尾声，地铁集团和腾达建设拒不承担责任。针对高教路站地铁施工对灵峰苑6、7幢造成的影响，尤其是造成墙体、地面等的开裂，我们强烈要求：1、由地铁方出资，聘请双方认可的第三方检测机构，对灵峰苑6、7幢进行房屋安全鉴定，明确地铁施工是否对房屋安全造成影响；2、在房屋安全不受影响的前提下，地铁方对居民家中和楼梯间开裂墙体、开裂地面和瓷砖、开裂天花板进行修复；3、地铁方对灵峰苑6、7幢隔音效果下降给出解决方案；4、针对地铁施工结束后，两幢楼的后续安全监测给出方案；5、第2条和第3条无法修复的部分，给出赔偿方案。         此件曾交办市地铁集团，反馈：3-2标业主代表韩旭（1524113262）已于2022年1月6日8时19分致电反映人，告知地铁方已于2021年 12月30日组织专题对接会（反映人本人也参会），会上明确灵峰苑房屋各项监测数据均正常，无房屋建筑结构沉降开裂等数据支持，不能满足其提出的几点诉求意见。地铁施工完成后将承担出入口周边小区设施（室外管道，室外铺装）的修复，对于楼内以及业主住宅内部的装修裂缝不在地铁完工后的修复范围。以上回复反映人未给予支持和理解。         现来电对反馈结果不认可，坚持称墙体开裂是2021年夏季地铁施工期间造成，认为专题对接会只凭地铁公司一方说辞不合理，要求找房屋安全鉴定中心专家一起出面，重新召开协调会，如一致认为居民房屋质量与地铁施工无关，在协调会上签字后其不再行政投诉，会自行通过法律途径维权，请相关部门帮助核实处理。       （原编号：WX20211588384451 系第二次交办）" ::
      "我是滨江区半岛水岸华庭业主，本小区正在召开关于物业选续聘的业主大会，昨天是开箱唱票的日子，现投诉以下事实1.天阳物业作为服务业主的公司，阻挠业主大会进程，有业主将其视频发到业主群，物业打电话威胁业主，扬言小区其他业主什么事都做得出来，性质如流氓黑社会！2.滨江住建局作为直管部门不作为，任由物业在小区胡闹，导致街道，社区工作人员焦头烂额！3.今天天阳物业叫来了1818黄金眼的记者，明知小区业主以年轻人居多，该时段大部分人都在上班，记者只逗留了很短时间，未进行全面走访，使得业主没有表达机会，希望媒体可以提前预约，联系业主安排合理采访时间。最后：选续聘是小区业主自己的事，集体的事情用集体的方式解决，昨晚公示的投票结果已经能反映本小区业主的意愿，天阳物业作为业主请来的管家，本当安守本分，但现在使出的这些手段，都已经让业主切身体会到实现业主权利之难，希望有关部门引起重视，尊重民意，构建和谐小区。" ::
      "龙港市江滨路福地华苑业主反映小区3栋1楼温州农丁农产品有限公司每天1点开始有车辆进出装卸货物，货物拆封、装卸的声音，工人聊天的声音过大，影响周边居民正常休息，望处理。我中心曾于DH20213618718775件交办，2021年10月9日龙港市基层治理委员会答复：“方岩社区已组织工作人员联合执法中队到现场调查情况，该公司负责人称准备搬离，正在另寻地方，需要时间。”现市民咨询搬离具体需要多久的时间，现建议在搬离期间夜间不要进行做工，白天做工可以，望采纳。" ::
      "反映：1、2021年9月左右，绍兴市越城区迪荡街道保利迪荡湖壹号21幢104、105、106室将南花园硬化，占地面积为20多平方，12月7日城管执法后，其继续违建。2、2021年8月，38幢103室南阳台飘窗改落地玻璃，北阳台水泥现浇外拓房间，已完工。3、29幢102、103、104室南阳台飘窗改落地玻璃，花园硬化，已完工。4、39幢101室南阳台飘窗改落地玻璃，花园硬化，已完工。（来电人不愿告知姓名）\n" + "诉求：希望部门核实，要求拆除违建，恢复绿化。" ::
      "反映：2021年11月30日17:13，我用13806759561拨打绍兴市生态环境局柯桥分局的电话84126287，反映绍兴市越城区北海街道景瑞曦之湖小区17-18幢对面的对旗山上焚烧工业垃圾一事，接听电话的工作人员表示7天内会给予答复，2021年12月3日22：59，我又再次拨打了该号码，工作人员表示不清楚，但是到现在都没有给予答复，我要求投诉环保局不作为。\n" + "诉求：希望部门核实查处。  （来电人执意要求反映） " ::
      Nil

    spark.udf.register("analysis", Udf.analysis _)
    val contentDF: DataFrame = contentList.toDF("content")
      //      .selectExpr("md5(content) as id", "split(analysis(content), ',') as word_list")
      .selectExpr("content as id", "split(analysis(content), ',') as words")
      .map {
        case Row(id: String, words: mutable.WrappedArray[String]) => {
          val onlyWords: mutable.Seq[String] = words.map(_.split("/")(0))
          (id, onlyWords)
        }
      }
      .toDF("id", "words")

    val countVectorizerModel: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("rawFeatures")
      .fit(contentDF)

    // TODO 此处相当于将所有分词后的数据全部加载进Driver的内存中，是不健康的操作。应该考虑用其他方式代替。
    val arr: Array[String] = countVectorizerModel.vocabulary
    val arr1: Array[Int] = arr.indices.toArray
    val indicesMap: Map[Int, String] = arr1.zip(arr).toMap
    val bc: Broadcast[Map[Int, String]] = spark.sparkContext.broadcast(indicesMap)

    val wordTf: DataFrame = countVectorizerModel.transform(contentDF)

    val wordTfIdf: DataFrame = new IDF()
      .setInputCol("rawFeatures")
      .setOutputCol("features")
      .fit(wordTf)
      .transform(wordTf)

    val eachWordDF: DataFrame = wordTfIdf
      .flatMap(
        row => {
          val id: String = row.getAs[String]("id")

          val indMap: Map[Int, String] = bc.value

          val rawFeatures: SparseVector = row.getAs[SparseVector]("rawFeatures")
          val rawFeaturesIndices: Array[Int] = rawFeatures.indices
          val rawFeaturesValues: Array[Double] = rawFeatures.values

          val features: SparseVector = row.getAs[SparseVector]("features")
          val featuresIndices: Array[Int] = features.indices
          val featuresValues: Array[Double] = features.values

          rawFeaturesIndices.zip(rawFeaturesValues).zip(featuresIndices).zip(featuresValues).map {
            case (((tfIndice, tfValue), tfIdfIndice), tfIdfValue) =>
              (id, indMap.get(tfIndice), tfIndice, tfValue, tfIdfIndice, tfIdfValue)
          }
        }
      )
      .toDF("id", "word", "tf_indice", "tf_value", "tf_idf_indice", "tf_idf_value")

    val topWords = eachWordDF
      .withColumn("rn",
        row_number().over(
          Window.partitionBy("id").orderBy(col("tf_idf_value").desc)
        )
      )
      .filter(col("rn") <= 10)
      .orderBy(col("id"), col("tf_idf_value").desc)
      .groupBy("id")
      .agg(collect_list(
        struct(
          col("word"),
          col("tf_indice"),
          col("tf_value"),
          col("tf_idf_indice"),
          col("tf_idf_value")
        )
      ).alias("topWords"))
      .select(col("id"), col("topWords").cast(StringType).alias("topWords"))

    topWords.write.format("com.crealytics.spark.excel")
      .option("header", "true")
      .save("output/TfIdfDemo/TfIdfDemo2.xlsx")

  }

}
