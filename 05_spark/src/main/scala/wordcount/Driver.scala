package wordcount

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Driver extends akka.Conf {
  def main(args: Array[String]): Unit = {
    //监控控制台
    val sys = ActorSystem("Driver", ConfigFactory.parseMap(getConf("2549")))

    val scanner = new java.util.Scanner(System.in)

    while (scanner.hasNext()) {
      val line = scanner.nextLine(); //获取到控制台一行

      //向MappersActor发送消息
      val mapperTask = MapperTask(line, (line: String) => {
        line.split(" ").toList.map { x => (x, 1) }
      })

      val mapperActor = sys.actorSelection("akka.tcp://mappers@127.0.0.1:2550/user/mappers")
      mapperActor ! mapperTask //将task发送给MapperActor
    }


  }
}