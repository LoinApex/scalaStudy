package course._05.actor.comm

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


//测试远程Actor
object RemoteActorServer {

  class ActorRm extends Actor {
    override def receive = {
      case msg: String => println(msg)
    }
  }

  def main(args: Array[String]): Unit = {
    //参数配置
    val conf = new java.util.HashMap[String, Object]()
    val IP = "127.0.0.1"
    val PORT = "2550"

    val list = new java.util.ArrayList[String]()
    list.add("akka.remote.netty.tcp")

    conf.put("akka.remote.enabled-transports", list) //参数是个集合
    conf.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider")
    conf.put("akka.remote.netty.tcp.hostname", IP)
    conf.put("akka.remote.netty.tcp.port", PORT)


    val sys = ActorSystem("master", ConfigFactory.parseMap(conf)) //以map方式读取配置文件
    //    Create new actor as child of this context with the given name
    sys.actorOf(Props[ActorRm], "im") //设定Actor的名字

    //访问地址akka.tcp://ActorSystem名称@IP:端口/user/im      [akka.tcp://master@127.0.0.1:2550]
  }
}

