package course._01.base

import scala.collection.mutable.ListBuffer

object ListTest {
  def main(args: Array[String]): Unit = {


    println("---------list----------------")
    //    list
    val list = List[Int]() //定义空list
    val li = List(1, 2, 3, 4) //初始化list
    println(li(2))
    val li2 = 22 +: li
    println(li2)
    println(33 :: li2)


    val buf1 = ListBuffer[Int]()
    val buf2 = ListBuffer(1, 3, 4, 5)
    val buf3 = ListBuffer(6, 7, 8, 9)

    //     buf2 +:3
    val x = List(1)
    val y = 2 +: x

  }

}
