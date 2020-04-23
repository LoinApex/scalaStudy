package course._06.wc.cons

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import course._05.actor.`trait`.reActorTracitConf

object Driver extends reActorTracitConf {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Driver", ConfigFactory.parseMap(getConf("2550")))
    val scanner = new java.util.Scanner(System.in)
    val mapper = system.actorSelection("akka.tcp://Mapper@127.0.0.1:2551/user/mapper")

    while (scanner.hasNext()) {
      val line = scanner.nextLine() // 要向mapper发送数据，还要发送操作数据的函数
      val init = 1
      val mapperTask = MapperTask(line, (line: String) => {
        // "hello world" => List((hello,1)  (world,1))
        line.replaceAll(",", "").split(" ").toList.map { x => (x, init) }
      })

      // 将任务发送给mapper
      mapper ! mapperTask
    }

  }
}
