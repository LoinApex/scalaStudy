package util

import java.util
import java.util.Properties

import org.yaml.snakeyaml.Yaml

object YamlUtil {

  private val conf=this.load("app.yml")


  def getNested(key: String): String = {
    if (conf == null) return null
    new YamlUtil(conf).getNested(key)
  }


  /**
    * 加载yaml文件
    * @param fileName
    * @return
    */
  //todo 未确定是否为单例，待查源码
  def load(fileName: String): util.Map[String, Object] = {
    new Yaml().loadAs(getClass.getResourceAsStream(s"/${fileName}"), classOf[util.HashMap[String, Object]])
  }

  /**
    * 简单处理，将yaml文件加载为传统的properties,不支持yaml列表
    * @param fileName
    * @return
    */
  def loadAsProperties(fileName: String): Properties = {
    val map: util.Map[String, Object] = load(fileName)
    val result = new util.HashMap[String,Object]
    buildFlattenedMap(result,map,null)
    val properties = new Properties
    properties.putAll(result)
    properties
  }

  private def buildFlattenedMap(result:util.Map[String,Object],source:util.Map[String,Object],path:String):util.Map[String,Object]={
    import scala.collection.JavaConversions._
    for ((key,value) <- source){
      var newPath: String = key
      if(null != path){
         newPath = s"$path.$key"
      }
      if(value.isInstanceOf[String]) {
        result += newPath -> value
      }else if(value.isInstanceOf[util.Map[String,Object]]){
        val map = value.asInstanceOf[util.Map[String,Object]]
        buildFlattenedMap(result,map,newPath)
      }else{
        result += newPath->(if(null != value) value else "")
      }
    }
    source
  }

  //隐式转换
  implicit def map2RichMap(map: util.Map[String, Object]) = new YamlUtil(map)
}


class YamlUtil(var map:util.Map[String,Object]) {

  //获取嵌套的key值
  def getNested(key: String):String= {
    var value: String = null
    val keys = key.split("\\.")
    for (item <- keys.zipWithIndex) {
      val (key, idx) = item

      val obj = map.get(key)
      if (obj == null) return null

      if (idx != keys.size - 1) {
        map = obj.asInstanceOf[util.Map[String, Object]]
      } else {
        value = obj.toString
      }
    }
    value
  }





}
