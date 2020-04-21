package course._05.actor

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

//配置文件方式
object ReActorClientConf {
  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("client", ConfigFactory.load().getConfig("ReActorClientConf"))
    //根据路径找到远程的Actor 指定server端口发送信息
    sys.actorSelection("akka.tcp://master@127.0.0.1:2555/user/im") ! "2556发出消息  20200421 17:32."
  }
}
