package akka

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSelection.toScala
import akka.actor.ActorSystem

object ClientActor extends Conf{
  def main(args: Array[String]): Unit = {
      //根据配置创建一个系统环境
      val sys = ActorSystem("client", ConfigFactory.parseMap(getConf("2551")))
      val actor = sys.actorSelection("akka.tcp://master@127.0.0.1:2550/user/jt")
      
      actor ! "我是client!"
  }
}