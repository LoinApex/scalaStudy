package course._06.wcount

import akka.actor.ActorSelection.toScala
import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import course._05.actor.`trait`.reActorTracitConf

class Mapper extends Actor {
  override def receive = {
    case MapperTask(line, mfunc) => {
      val result = mfunc(line)
      for (r <- result) {
        println(r)
        //继续转发处理 发消息给reducer
        Mapper.getReducer() ! ReducerTask(r, (a, b) => a + b) // 相同key的数据要发给同一个reducer
      }
      /*
        (hello,1)
        (hello,1)
        (world,1)
        (hello,1)
        (spark,1)			(1,1) => 1+1            mapper端的优化 map side combine
       */
    }
  }
}

object Mapper extends reActorTracitConf { // object 里定义的方法相当于java static
  def getReducer(): ActorSelection = {
    reducer
  }

  var reducer: ActorSelection = null;

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Mapper", ConfigFactory.parseMap(getConf("2551")))
    system.actorOf(Props[Mapper], "mapper")
    // akka.tcp://Mapper@127.0.0.1:2551/user/mapper
    reducer = system.actorSelection("akka.tcp://Reducer@127.0.0.1:2554/user/reducer")
  }

}
