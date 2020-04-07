package wordcount

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Reducers extends Actor {
  //全局变量，输入值累加

  import scala.collection.mutable.Map //作用域就变小了
  val combine = Map[String, Int]()

  override def receive = {
    case ReduceTask((key, value), rfun) => {
      val v = combine.getOrElse(key, 0) //设置默认值，如果key在map中存在，返回这个key，如果这个key不存在，返回0,默认值
      combine += key -> rfun(v, value)

      println(combine)
      println("finish.")
    }
  }
}

object Reducers extends akka.Conf {
  def main(args: Array[String]): Unit = {
    //创建actor链接
    val sys = ActorSystem("reducers", ConfigFactory.parseMap(getConf("2551")))
    sys.actorOf(Props[Reducers], "reducers") //远程访问链接地址
  }
}