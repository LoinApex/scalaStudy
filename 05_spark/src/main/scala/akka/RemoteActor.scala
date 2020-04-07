package akka

import akka.actor.Actor
import Actor._
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props

class Actor4() extends Actor{
  override def receive = {
    case msg:String => println(msg)
  }
}

object RemoteActor extends Conf{
  def main(args: Array[String]): Unit = {
   
    //根据配置创建一个系统环境
    val sys = ActorSystem("master", ConfigFactory.parseMap(getConf("2550")))
    sys.actorOf(Props[Actor4],"jt")
    //访问链接：akka.tcp://127.0.0.1@master:2550/user/jt
    
  }
}