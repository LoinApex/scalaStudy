package util

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}

object CacheUtil {

  val cache: LoadingCache[String, AnyRef] = CacheBuilder.newBuilder().build(new CacheLoader[String, AnyRef]() {
    override def load(key: String): AnyRef = {
      null
    }
  })

  def get(key: String): AnyRef = {
    cache.get(key)
  }

  def put(key: String, value: AnyRef): Unit = {
    cache.put(key, value)
  }

}
