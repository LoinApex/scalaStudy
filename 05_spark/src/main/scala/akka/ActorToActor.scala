package akka

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem

//定义第一个Actor
class Actor1() extends Actor{
  override def receive = {
    case msg:String =>{
      println("Actor1接收到的消息："+msg)
      //给Actor2发送消息
      var a2 = context.actorOf(Props[Actor2])  //从上下文件对象中context获取到Actor2对象
      a2 ! "Actor2发送消息"
    }
  }
}

//定义第二个Actor
class Actor2() extends Actor{
  override def receive = {
    case msg:String => {
      println("Actor2接收到的消息："+msg)
    }
  }
}

object ActorToActor {
  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("sys")    //创建系统环境
    var a1 = sys.actorOf(Props[Actor1])  //从系统环境中获取Actor1对象
    a1 ! "System发送的消息"
  }
}