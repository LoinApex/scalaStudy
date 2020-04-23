package course._05.actor.`trait`

trait reActorTracitConf {
  /**
   * 通过接口方式 构建actor
   *
   * Scala Trait(特征) 相当于 Java 的接口，实际上它比接口还功能强大。
   * 与接口不同的是，它还可以定义属性和方法的实现。
   * 一般情况下Scala的类只能够继承单一父类，但是如果是 Trait(特征) 的话就可以继承多个，从结果来看就是实现了多重继承。
   * https://www.runoob.com/scala/scala-traits.html
   */
  def getConf(PORT: String) = {
    val conf = new java.util.HashMap[String, Object]()
    val IP = "127.0.0.1"

    val list = new java.util.ArrayList[String]()
    list.add("akka.remote.netty.tcp")

    conf.put("akka.remote.enabled-transports", list) //参数是个集合
    conf.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider")
    conf.put("akka.remote.netty.tcp.hostname", IP)
    conf.put("akka.remote.netty.tcp.port", PORT)

    conf //scala不用return语句，最后一句就作为返回的对象
  }


}
