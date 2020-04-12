package course._01.base

import org.junit.Test

import scala.collection.mutable

class DataOper {
  @Test
  def t1(): Unit = {
    //    字符串使用

    val a = 10
    val s1 = "the value is: " + a //传统方式

    // s : StringContext
    val s2 = s"the value is: ${a}" //类似jstl获取上面定义的常量
    val s3 = s"the value is: $a" //缩写方式
    println(s1)
    println(s2)
    println(s3)

    //使用"""进行字符串定义
    val json = "{\"name\":\"张三\"}"
    val json2 = """{"name":"张三"}"""
    println(json)
    println(json2)


  }

  @Test
  def t11() {

    /*
     * 映射：
     * 包含 一组 键值对 元素的集合
     * 只是一个对应的查询，类似于指针
     */

    val Z = Map(1 -> 2, 5 -> 4, 3 -> 5)

    val a = Z(1)
    println(a)

    //检查是否有某个键

    val c = Z.contains(1) //boolean类型
    println(c)

    val d = Z.getOrElse(1, 0) //找到返回1  没有返回0


    //变长
    var W = Z + (3 -> 3)
    W += (5 -> 4, 7 -> 4) //增加映射
    W -= 3 //较少 直接使用  键值
    println(W.contains(3))



    //  映射的枚举
    for ((i, j) <- Z) print(i, j) // 操作
    // 互换映射的键值
    for ((i, j) <- Z) yield (j, i)
    // 获取映射内键的集合或值的集合
    val e = Z.keySet
    val f = Z.values
    // 可以利用for表达式只枚举映射的键或者集合

    /**
     * 元祖
     */
    val on = Array('a', 'b', 'c')
    val wo = Array(1, 2, 3)

    //生成数组

    //  val thr = on.zip(wo)
    val thr = on zip wo
    val fou = on.zip(wo).toMap


    /*  {……}号之间声明的量只有在{}内是有意义的
      {val a=0... {...val b=a}...}// 内层能调用外层声明的量
      {val a=b...{val b=0...}...} //外层不能调用内层声明的量
      内层声明与外层声明相同时，内层使用的是内层的声明，外层使用的是外层的声明
      如：
       */
    val x = 2
    for (i <- 1 to 4) print(x + i)

  }

  @Test
  def t2: Unit = {
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

    val aa = {
      for (i <- "HELLO") yield i
    };
    for (ii <- aa) println(ii)

    val bb = {
      for (i <- "HELLO"; j <- 1 to 2) yield (i + j).toChar; //加法运算会转换为整型；char int 转换为整型数字，由第一个决定
    }
    for (jj <- bb) println("bbbbbbbbb>>>>>" + bb)

    for (i <- 1 to 2) yield i.toChar;
    for (i <- 1 to 2; j <- "HELLO") yield (i + j).toChar

    println("<<<<<<<<<<<<<<<<<<<<< ");

    val numList = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // for 循环
    val retVal = for {
      a <- numList
      if a != 3; if a < 8
    } yield a

    // 输出返回值
    for (a <- retVal) {
      println("Value of a: " + a);
    }


    /**
     * 队列操作
     * http://blog.csdn.net/lovehuangjiaju/article/details/46984575
     */
    val que = scala.collection.mutable.Queue(1, 2, 3)
    que.enqueue(4) //入队
    println(que.toList)
    println(que.dequeue())
    println(que.toList)


    println("-------------stack------------")
    /**
     * 栈操作
     */
    val sta = new mutable.Stack[Int]
    sta.push(1)
    sta.push(2) //入栈
    sta.push(3)
    sta.push(4)
    sta.foreach(print)
    println("pop  " + sta.pop) //出栈
    sta.foreach(print)

  }
}