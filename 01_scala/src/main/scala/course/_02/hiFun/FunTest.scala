package course._02.hiFun

import org.junit.Test


class FunTest {

  /**
   * partition 拆分
   */

  @Test
  def par: Unit = {
    val array = Array(1, 2, 3, 4, 5)
    val tuple = array.partition((x: Int) => (x % 2 != 0))
    println(tuple._1.toIterable)
    println(tuple._2.toIterable)
  }

  /**
   * map映射
   * 对原数据处理
   */
  @Test
  def mapTest: Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    //加上prefix和suffix，并自动转换为字符串类型
    list.map { x => x + 1 }.map { x => "/WEB-INF/view/" + x + ".jsp" }.foreach(x => println(x))

  }

  /**
   * mapValues映射值
   */
  @Test
  def mapvalTest: Unit = {

  }

  /**
   * reduce化简
   * 对数据进行自定义运算
   */
  @Test
  def reduceTest: Unit = {
    val list = Array(1, 2, 3, 4)
    list.reduce { (x, y) => x + y }
    list.reduce(_ + _)
    val i = list.reduce { (x, y) => println(s"x:$x y:$y"); x + y } //两条语句,外层须是大括号
    val ii = list.reduce((x, y) => x + y)
    println(i)
    println(ii)
  }

  /**
   * groupby 分组
   */
  @Test
  def groupbyTest: Unit = {
    val list = List(("北京", 1), ("上海", 3), ("武汉", 6), ("天津", 2), ("上海", 1), ("武汉", 1))
    val tupl = list.groupBy(x => x._1)
    println(tupl)


  }

  /**
   * par 并行
   * parallel平行的
   * Returns a parallel implementation 返回一个平行实现,多线程并行计算
   * 加法交换律/结合律,数据累加不分先后数据,并行对结果没有影响
   * ForkJoinPool 是 Java SE 7 新功能“分叉/结合框架”的核心类。
   */
  @Test
  def parTest: Unit = {
    val startTime = System.currentTimeMillis(); //开始毫秒数
    val list = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val res = list.par.reduce { (x, y) =>
      println(s"x:${x},y:${y}  >" + Thread.currentThread().getName); //当前处理线程名称
      x + y
    }
    println(res)
    println(System.currentTimeMillis() - startTime)


  }


  @Test
  def test: Unit = {
    println("----filter过滤-----")
    val list = List(1, 2, 3, 4, 5, 6)
    list.filter { x => x % 2 == 1 }.foreach(x => println(x))

    println("----flatte拍扁-----")
    val lll = List(List(1, 2, 3), List(4, 3, 6))
    val lll2 = lll.flatten
    println(lll2)
    lll.flatMap(_.map(_ * 2)).foreach(x => println(x)) // flatMap对嵌套list进行操作

    println("----zip匹配-----")
    val zalist = List("sdaf", "fdsa", "fdsa")
    println(zalist.zip(list))


  }


}
