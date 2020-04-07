package course._01.base

import scala.collection.mutable.ArrayBuffer

/**
 * scala特殊符号使用
 * https://www.zybuluo.com/boothsun/note/1014438
 */
object SymbolTest {

  def main(args: Array[String]) {
    // ::: 三个冒号:2个集合合并
    println("------- [:::] ")
    val list1 = List(1, 2)
    val list2 = List(3, 4)

    val listCombine = list1 ::: list2
    listCombine.foreach(x => {
      println(x)
    })

    println("------- [::] ")
    // :: 2个 冒号 元素添加到集合中
    // 元素在前, 集合在后
    val d = 5 :: listCombine

    // => 方法体指标符
    d.foreach(x => {
      println(x)
    })

    // -> 构造一个二元的元组
    println("------- [->]")
    val one = 1
    val two = 2
    val c = one -> two
    println(c)

    // _N 用于访问元组中的数据元素,坐标从1开始
    println("------- [_N]")
    println("one = " + c._1)
    println("two = " + c._1)

    println("------- [<-] ")
    // <- 将集合中的元素 依次赋给临时变量
    for (v <- listCombine) {
      println(" v = " + v)
    }

    println("------- arrBuf1 [result1]")
    //
    val arrBuf1 = new ArrayBuffer[Int]()
    arrBuf1.+=(11) // 添加一个元素
    arrBuf1.+=(33, 44) //添加多个元素
    arrBuf1.insert(1, 22) //在index=1,位置插入22
    arrBuf1 += 55 //添加一个元素
    arrBuf1.append(66) // 追回一个元素
    arrBuf1.prepend(77) // 在头部追加一个元素
    arrBuf1.prepend(88) // 在头部追加一个元素
    arrBuf1.prepend(99) // 在头部追加一个元素
    for (elem <- arrBuf1) {
      println(elem)
    }
    println("------- arrBuf1 [result2] ")
    arrBuf1.remove(3, 2) // 从index=3开始,删除2个元素
    for (elem <- arrBuf1) {
      println(elem)
    }

    println("------- arrBuf1 [result3] ")
    arrBuf1.trimEnd(2) // 删除尾部2个元素
    for (elem <- arrBuf1) {
      println(elem)
    }

    val arrBuf3 = arrBuf1.+:(3) // 在首部增加一个元素,并返回一个新集合
    println("------- arrBuf3 [result] = ")
    arrBuf3.foreach(x => {
      println(x)
    })

    println("------- arrBuf2 [result]")
    // val arrBuf3 = 5 ++ arrBuf2
    val arrBuf2 = new ArrayBuffer[Int]()
    arrBuf2.+=(3)
    arrBuf2.+=(4)

    arrBuf2.foreach(x => {
      println(x)
    })

    println("------- arrBufCom [result]")
    // 将arrBuf2,arrBuf1所有元素都添加到新集合 arrBufCom 中,返回arrBufCom
    val arrBufCom = arrBuf1.++:(arrBuf2)
    arrBufCom.foreach(x => {
      println(x)
    })

    println("------- arrBuf1 [result4]")
    // 将arrBuf2中的所有元素,都增加到arrBuf1首部
    arrBuf1.++=:(arrBuf2)
    arrBuf1.foreach(x => {
      println(x)
    })

    // :_* 将集合元素当作 参数序列来处理,用在有变长参数列表的函数中
    println("------- seq:_* [result]")
    val seq = Seq(1, 2, 3, 4, 5)
    println(my_sum(seq: _*))
  }

  def my_sum(args: Int*): Int = {
    var result = 0
    for (a <- args) {
      result += a
    }
    return result
  }
}
