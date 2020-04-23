package course._03.fun

import java.io.PrintWriter

import org.junit.Test

import scala.io.Source

/**
 * 高阶函数使用
 */
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
   * Builds a new collection by applying a function to all elements of this general collection.
   * 使用一个函数 对该集合的所有元素构造一个新的集合
   */
  @Test
  def mapTest: Unit = {
    val list = List(1, 2, 3, 4, 5, 6)
    //加上prefix和suffix，并自动转换为字符串类型
    list.map { x => x + 1 }.map { x => "/WEB-INF/view/" + x + ".jsp" }.foreach(x => println(x))

  }


  /**
   * reduce化简
   * 对数据进行自定义运算
   * Reduces the elements of this collection or iterator using the specified associative binary operator.
   * 使用函数减少元素
   * The order in which operations are performed on elements is unspecified and may be nondeterministic.
   *
   *
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
   * 查看中间结果,可以使用shell
   */
  @Test
  def groupbyTest: Unit = {
    val list = List(("北京", 1), ("上海", 3), ("武汉", 6), ("天津", 2), ("上海", 1), ("武汉", 1))
    //    1.按照key进行分组
    val groupbyMap = list.groupBy(x => x._1) //对list按照key进行分组
    println(groupbyMap)

    //    Map(上海 -> List((上海,3), (上海,1)), 北京 -> List((北京,1)),
    //    天津 -> List((天津,2)), 武汉 -> List((武汉,6), (武汉,1)))

    /*
     * mapValues映射值 转换
     * Transforms this map by applying a function to every retrieved value.
     * 使用一个函数对检索到的每个值进行转换
     */

    println(list.map(x => x._2))
    //   List(1, 3, 6, 2, 1, 1)

    //    2.取出value值
    //    3.根据key值对value值合并

    val res = groupbyMap.mapValues(list => list.map(x => x._2).reduce(_ + _))
    println(res)

  }

  /**
   * 求和
   */
  @Test
  def wordsCount: Unit = {
    val list = List(("北京", 1), ("上海", 3), ("武汉", 6), ("天津", 2), ("上海", 1), ("武汉", 1))
    //使用key对value进行求和
    val res = list.groupBy(x => x._1).mapValues(x => x.map(x => x._2).reduce(_ + _))
    res.foreach(println)
  }

  /**
   * wordsCount,思路: 使用mapValues函数依据key对value进行合并&求和
   */
  @Test
  def wordsCount2: Unit = {
    val line = Source.fromFile("src/main/resources/aa.txt").getLines()
    val mv = line.toList.map(x => (x, 1)).groupBy(x => x. _1).mapValues(i => i.map(t => t._2).reduce((x, y) => x + y))
    val list = mv.foreach(print)

  }


  /**
   * wordscount简化例子
   */
  @Test
  def wordsCount3: Unit = {

    val list = Source.fromFile("c:/words.txt").getLines().toList
      .map { x=> (x,1) }.groupBy { x=> x._1 }
      .mapValues{ list => list.map { tuple => tuple._2 }.reduce {(x,y) => x+y} }
      .foreach(x=>println(x))

    val list1= Source.fromFile("c:/words.txt").getLines().toList
      .map { (_,1) }.groupBy { _._1 }.mapValues{ _.map{ _._2 }.reduce{ _+_ } }
    //结果保存到文件中
    val out = new PrintWriter("c:/result.txt")
    for (x <- list1) out.println(x)
    out.close

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
    val list = List(1, 2, 3, 4, 5, 6)

    println("----filter过滤-----")
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
