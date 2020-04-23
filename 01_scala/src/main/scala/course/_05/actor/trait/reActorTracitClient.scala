package course._05.actor.`trait`
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object reActorTracitClient extends reActorTracitConf {


  def main(args: Array[String]): Unit = {
    //参数配置
    val sys = ActorSystem("master", ConfigFactory.parseMap(getConf("2551")))
    //根据路径找到远程的Actor
    sys.actorSelection("akka.tcp://master@127.0.0.1:2550/user/jt") ! "2551发出消息."
  }


}
