package course._01.base

import scala.collection.mutable.ArrayBuffer

object ArrayTest {
  def main(args: Array[String]): Unit = {
    //    数组元素+1
    val arr = Array(1, 2, 3, 4, 5)
    for (a <- arr) println(a + 1)


    //yield把结果放到一个新的数组中
    val array = Array(1, 2, 3, 4, 5) //声明数组
    val array2 = for (x <- array) yield x + 1
    for (y <- array2) println(y)

    println("---------可变ArrayBuffer-------------")

    //可变数组
    val buffer = ArrayBuffer(1, 2, 3, 4, 5)
    buffer += 6
    println(buffer)
    buffer.append(22, 33)//增加
    println(buffer)
    buffer.remove(0)//删除 下标
    println(buffer)


    println("---------set-------------")


    val se = Set(1, 2, 1, 3, 45)
    println(se)

    println("----------map---------------")
    val map = Map(1 -> "北京", 2 -> "上海", 3 -> "广州")
    println(map(1))
    val map1 = map + (4 -> "河北")
    println(map1(4))


  }

}
