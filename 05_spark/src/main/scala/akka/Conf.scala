package akka

//定义一个抽象类，实现了getConf方法，获取配置参数，PORT是可以动态配置
trait Conf {
  def getConf(PORT:String) = {
    val conf = new java.util.HashMap[String,Object]()
    val IP = "127.0.0.1"

    val list = new java.util.ArrayList[String]()
    list.add("akka.remote.netty.tcp")
    
    conf.put("akka.remote.enabled-transports", list)  //参数是个集合
    conf.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider")
    conf.put("akka.remote.netty.tcp.hostname", IP)
    conf.put("akka.remote.netty.tcp.port", PORT)
    
    conf
  }
}