package course._05.actor.`trait`

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object reActorTracitServer extends reActorTracitConf {

  class TracitAc extends Actor {
    override def receive = {
      case msg: String => println(msg)
    }
  }

  def main(args: Array[String]): Unit = {
    //参数配置
    val sys = ActorSystem("master", ConfigFactory.parseMap(getConf("2550")))
    sys.actorOf(Props[TracitAc], "jt") //设定Actor的名字
  }
}
