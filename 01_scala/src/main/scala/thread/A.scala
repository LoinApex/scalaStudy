package thread

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object TestConcurrent {
  def main(args: Array[String]): Unit = {
    //创建了Future对象，scala就把它关联到线程池，然后执行future中的代码，返回结果就自动触发了onSuccess事件通过case语句就可以得到返回结果。
    val fu = Future {
      println("开始运行计算")
      Thread.sleep(200)
      100 //返回值
    }

    fu.onSuccess { //成功后通过此事件触发
      case x => println(x+"fdkslkkjfl")//case接收值
    }

    Thread.sleep(3000) //主线程等3秒，必须写才可以执行
  }
}
