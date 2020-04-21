package course._05.actor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory


object RemoteActorClient {
  def main(args: Array[String]): Unit = {
    //参数配置
    val conf = new java.util.HashMap[String, Object]()
    val IP = "127.0.0.1"
    val PORT = "2551" //更改端口

    val list = new java.util.ArrayList[String]()
    list.add("akka.remote.netty.tcp")

    conf.put("akka.remote.enabled-transports", list) //参数是个集合
    conf.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider")
    conf.put("akka.remote.netty.tcp.hostname", IP)
    conf.put("akka.remote.netty.tcp.port", PORT)

    val sys = ActorSystem("client", ConfigFactory.parseMap(conf))
    //根据路径找到远程的Actor 发送信息
    sys.actorSelection("akka.tcp://master@127.0.0.1:2550/user/im") ! "2551发出消息  20200421 17:32."
  }
}
