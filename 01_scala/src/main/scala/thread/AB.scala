package thread

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object CCCCC {
  def main(args: Array[String]): Unit = {
    val fu = Future {
      println("开始运行计算111111")
      Thread.sleep(200)
      100 //返回值
    }

    //也可以主线程得到返回值，主线程是阻塞的
    val r = Await.result(fu, Duration.Inf) //持久化：永久
    println(r)
    Thread.sleep(500)
  }
}
