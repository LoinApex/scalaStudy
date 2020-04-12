package course._01.base

/**
 * 方法定义
 *
 */
object DivDef {

  def main(args: Array[String]): Unit = {
    val d = div(10, 2)
    println(d)

    println(divO(10, 0))

    //变量使用: var可变变量使用较少, 避免在不同节点运行使用不同值
    var y = 10
    println(y)
    y = 20
    println(y)

    // 表达式
    val c = if (y == 20) "ok" else "no ok"
    println(c)
    println("----------------------------")
  }

  //  定义除法
  def div(a: Int, b: Int): Int = {
    a / b //约定最后一行为返回值
  }

  def divO(a: Int, b: Int): Option[Int] = {
    if (b != 0) Some(a / b) else None
  }

  def ?(a: Int, b: Int): Unit = {
    if (b != 0) Some(a / b) else None

  }

}
