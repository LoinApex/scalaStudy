package course._01.base

import org.junit.Test

object TupleTest {


}

/**
 * 元组
 * 与列表一样，元组也是不可变的，但与列表不同的是元组可以包含不同类型的元素。
 */
class TupleTest {
  @Test
  def t1: Unit = {
    val t = (4, 3, 2, "上海")
    val t1 = new Tuple3(1, 3.14, "Fred")

    t.productIterator.foreach { i => println("Value = " + i) }
    println(t1._3)


  }
}