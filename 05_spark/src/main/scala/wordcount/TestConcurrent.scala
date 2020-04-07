package wordcount

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future


object TestConcurrent {
  def main(args: Array[String]): Unit = {
    //定义Future，实现线程并发
    var fu = Future{
      println("future starting...")
      Thread.sleep(200)
      "100"
    }
    
    //成功会触发下面的方法
    fu.onSuccess{
      case y:String => println(y)
    }
    
    
    Thread.sleep(500)
  }
}