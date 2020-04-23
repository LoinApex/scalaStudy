package course._05.actor.conf

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

//测试远程Actor 配置文件方式
object ReActorServerConf {

  class ActorRm extends Actor {
    override def receive = {
      case msg: String => println(msg)
    }
  }

  def main(args: Array[String]): Unit = {

    val sys = ActorSystem("master", ConfigFactory.load().getConfig("ReActorServerConf"))//application.conf 中服务端
    sys.actorOf(Props[ActorRm], "im") //设定Actor的名字
    //访问地址akka.tcp://ActorSystem名称@IP:端口/user/im      [akka.tcp://master@127.0.0.1:2555]
  }
}
