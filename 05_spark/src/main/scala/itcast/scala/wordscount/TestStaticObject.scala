package itcast.scala.wordscount

import scala.collection.mutable.ArrayBuffer


/**
 * Java静态方法在scala中的实现   
 */
class TestStaticObject {

}
object StaticFunciton {
  def add(x: Int, y: Int) = {
    x + y
  }
}

object TestStaticObject {
  def main(args: Array[String]): Unit = {
    val aa = StaticFunciton.add(2, 3)
    print(aa)
    
    
    val buf=new ArrayBuffer()
    
    
    
    
    
  }
}

