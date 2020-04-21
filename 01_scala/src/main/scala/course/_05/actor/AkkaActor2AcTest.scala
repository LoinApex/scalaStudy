package course._05.actor

import akka.actor.{Actor, ActorSystem, Props}

/**
 * 使用akka中的actor相互发送消息
 */
object AkkaActor2AcTest {
  def main(args: Array[String]): Unit = {

    val sys = ActorSystem("sys") //创建运行环境
    //由系统创建actor，classOf[Actor1]获得Actor1类
    //val a = sys.actorOf(Props(classOf[Actor1]))
    val a = sys.actorOf(Props[Actor1])
    a ! "这是发给Actor1的消息"


  }
}

class Actor1() extends Actor {
  override def receive = {
    case msg: String => {
      println("Actor1 收到的消息是：" + msg)
      //由上下文创建actor，context actor的上下文对象
      val b = context.actorOf(Props[Actor2])
      b ! "这是发给Actor2的消息"
    }
  }
}

class Actor2 extends Actor {
  override def receive = {
    case msg: String => {
      println("Actor2 收到的消息是：" + msg)
    }
  }
}

