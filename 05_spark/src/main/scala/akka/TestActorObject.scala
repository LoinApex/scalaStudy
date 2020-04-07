//package akka
//
//import scala.actors.Actor.actor
//import scala.actors.Actor.receive
//
////定义一个类，必须序列化
//class Student1(val name:String, val age:Int) extends Serializable{}
//
////定义一个case类
//case class Student2(val name:String, val age:Int){}
//
//object TestActorObject {
//  def main(args: Array[String]): Unit = {
//    val badActor = actor{
//      receive{
//        case stu:Student1 => println(stu.name)
//        case stu:Student2 => println(stu.name+" "+stu.age)
//      }
//    }
//
//    //创建一个Student1对象
//    val stu1 = new Student1("tony",18)
//    //badActor ! stu1
//
//    val stu2 = Student2("hellen",16)
//    badActor ! stu2
//  }
//}