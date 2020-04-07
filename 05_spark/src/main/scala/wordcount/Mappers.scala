package wordcount

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Mappers extends Actor {
  override def receive = {
    case MapperTask(line, mfun) => {
      val result = mfun(line)
      for (r <- result) {
        println(r)

        //发消息给Reducers
        val reducers = Mappers.getReducer()
        reducers ! ReduceTask(r, (a, b) => (a + b))
      }
    }
  }
}

//如果object和class名称一样，object对象就成为伴生对象
object Mappers extends  akka.Conf {

  def getReducer(): ActorSelection = {
    reducer
  }

  var reducer: ActorSelection = null;

  def main(args: Array[String]): Unit = {
    //创建actor链接
    val sys = ActorSystem("mappers", ConfigFactory.parseMap(getConf("2550")))
    sys.actorOf(Props[Mappers], "mappers") //远程访问链接地址

    reducer = sys.actorSelection("akka.tcp://reducers@127.0.0.1:2551/user/reducers")
  }
}