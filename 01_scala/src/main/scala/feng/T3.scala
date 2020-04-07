package scala.feng

import scala.collection.mutable.Stack

object T3 {

  def main(args: Array[String]) {
    /**
     * <b>
     * 循环
     * </b>
     *
     */

    //   scala中的集合分为两种，一种是可变的集合，另一种是不可变的集合
    //可变的集合可以更新或修改，添加、删除、修改元素将作用于原集合
    //不可变集合一量被创建，便不能被改变，添加、删除、更新操作返回的是新的集合，老集合保持不变

    // 1 to 4  1<=i<=4
    // 1 until 4  1<=i<= 4-1  不包含上界

    //  <- 赋值符号

    for (i <- -1 to 4) {
      println(i)

    }

    println("<><><><><")

    /**
     * 使用if嵌套
     * 嵌套枚举
     */
    for (i <- -1 to 4; if i % 2 == 0; if i != 2) println(i)
    println("<><><><><")

    for (a <- 1 to 3; b <- 1 to 4) {
      println("Value of a: " + a);
      println("Value of b: " + b);
    }
    println("==================")

    /**
     * yield 把每次枚举值保存在集合中
     */

    val No =
      for (i <- 1 to 4)
        yield i

    //  NO: scala.collection.immutable.IndexedSeq[Int] = Vector(1, 2, 3, 4)

    val aa = { for (i <- "HELLO") yield i };
    for (ii <- aa) println(ii)

    val bb = {
      for (i <- "HELLO"; j <- 1 to 2) yield (i + j).toChar; //加法运算会转换为整型；char int 转换为整型数字，由第一个决定
    }
    for (jj <- bb) println("bbbbbbbbb>>>>>" + bb)

    for (i <- 1 to 2) yield i.toChar;
    for (i <- 1 to 2; j <- "HELLO") yield (i + j).toChar

    println("<<<<<<<<<<<<<<<<<<<<< ");

    var a = 0;
    val numList = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // for 循环
    var retVal = for {
      a <- numList
      if a != 3; if a < 8
    } yield a

    // 输出返回值
    for (a <- retVal) {
      println("Value of a: " + a);
    }

  }
  ////////////////////////////////////////

  /**
   * 队列操作
   * 	http://blog.csdn.net/lovehuangjiaju/article/details/46984575
   */
  var queue = scala.collection.immutable.Queue(1, 2, 3)

  queue.dequeue

  /**
   * 栈操作
   */
  //   import scala.collection.mutable.Stack
  val stack = new Stack[Int]

}