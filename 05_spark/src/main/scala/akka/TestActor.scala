//package akka
//
//import scala.actors._, Actor._
//
//object TestActor {
//  def main(args: Array[String]): Unit = {
//    //创建一个Actor
//    val badActor = actor {
//      //成功完成触发事件，接收只要有一个满足条件的就进行处理，然后结束
//      receive {
//        case msg: String => println(msg)
//        case msg: Int => {
//          val r = msg + 10
//          println(r)
//        }
//      }
//    }
//
//    //发送消息，
//    badActor ! 100
//    badActor ! "今天学习scala晕不晕!"
//  }
//}