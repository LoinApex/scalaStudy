package course._06.wcount

import akka.actor.Actor
import com.typesafe.config._
import akka.actor.ActorSystem
import akka.actor.Props
import course._05.actor.`trait`.reActorTracitConf
import scala.collection.mutable.Map


// 负责化简，聚合结果
class Reducer extends Actor {
  val combiner = Map[String, Int]() // (hello,3) (world,1)

  override def receive = {
    case ReducerTask((key, value), rfunc) => { // (a,b)=>a+b
      val v = combiner.getOrElse(key, 0) // 根据key找，如果有key，返回值否则返回一个默认值
      combiner += key -> rfunc(v, value)

      println(combiner)
      println("finished.")
    }
  }

  /*
   *
(hello,1)
(hello,1)
   */
}

object Reducer extends reActorTracitConf {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Reducer", ConfigFactory.parseMap(getConf("2554")))
    system.actorOf(Props[Reducer], "reducer")
    // akka.tcp://Reducer@127.0.0.1:2554/user/reducer
  }
}
