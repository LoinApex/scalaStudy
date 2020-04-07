package thread

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TestConcurrentaa {
  def main(args: Array[String]): Unit = {
    val fu1 = Future {
      println("f1开始运行计算")
      Thread.sleep(200)
      100
    }

    val fu2 = Future {
      println("f2开始运行计算")
      Thread.sleep(300)
      200
    }

    //yield把结果放到一个新的数组中
    val c = for (a <- fu1; b <- fu2) yield (a + b) //并发的
    println(Await.result(c, Duration.Inf)) //阻塞的
  }
}
