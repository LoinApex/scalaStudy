package wordcount

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{Future, _}
import scala.concurrent.duration.Duration

object TestMuchFuture {
  def main(args: Array[String]): Unit = {
    var fu1 = Future{
    	println("fu1 starting...")
      Thread.sleep(200)
      100
    }
    
    var fu2 = Future{
      println("fu2 starting...")
      Thread.sleep(300)
      200
    }
    
    //将两个Future对象中的多个线程进行返回值进行累加
    var r = for(a <- fu1;b <- fu2) yield (a+b)
    
    //结果合并输出，阻塞
    var a = Await.result(r, Duration.Inf)
    println(a)
  }
}