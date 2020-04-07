package util

import io.codis.jodis.RoundRobinJedisPool
import redis.clients.jedis.JedisPoolConfig


/*
 * 描述信息  
 * User: Qing  
 * Date 2019/12/24
 * Version 1.0  
 */
object JodisUtil  {

  private val jedisPoolConfig = new JedisPoolConfig()
  jedisPoolConfig.setMaxTotal(1000)
  jedisPoolConfig.setMaxIdle(50)
  jedisPoolConfig.setMaxIdle(3)
  jedisPoolConfig.setMaxWaitMillis(100000)
  jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(5000000)

  private val jedisPool = RoundRobinJedisPool.create()
    .curatorClient("cdh2:2181,cdh3:2181,cdh4:2181,cdh5:2181,cdh6:2181", 500000)
    .zkProxyDir("/jodis/codis-test1")
    .database(1)
    .timeoutMs(2000000)
    .soTimeoutMs(5000000)
    .password("nw..Redis216")
    .poolConfig(jedisPoolConfig)
    .build()


  def getJedis()=jedisPool.getResource()


  def main(args: Array[String]): Unit = {
    //val map=getJedis().hkeys("DIM:WIDE:JLD_DJMX")
   // val map=getJedis().hgetAll("DIM:WIDE:JLD_DJMX")
    val v=getJedis().get("foo")
    println(v)
  }
}
