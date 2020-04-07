package util

import java.util.Properties

object PropUtil {

  /**
    * 获取配置文件Properties对象
    * @author make
    * @return java.util.Properties
    */
  def load(fileName:String) :Properties = {
    val properties = new Properties()
    //读取源码中resource文件夹下的my.properties配置文件,得到一个properties
    val reader = getClass.getResourceAsStream(s"/${fileName}")
    properties.load(reader)
    properties
  }



}
