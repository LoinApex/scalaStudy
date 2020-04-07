package wordcount

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{Future, _}
import scala.concurrent.duration.Duration

object TestPauseConcurrent {
  def main(args: Array[String]): Unit = {
    //定义Future，实现线程并发
    var fu = Future{
      println("future starting...")
      Thread.sleep(200)
      "100"
    }
    
    //阻塞，等待所有线程完成，Duration.Inf持续化
    val r = Await.result(fu, Duration.Inf)
    println(r)
    Thread.sleep(500)
  }
}