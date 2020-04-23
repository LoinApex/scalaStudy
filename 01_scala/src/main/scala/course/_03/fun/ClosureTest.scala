package course._03.fun

object ClosureTest {
  /**
   * 闭包
   * https://www.runoob.com/scala/scala-closures.html
   */
  def main(args: Array[String]) {
    println("muliplier(1) value = " + multiplier(1))
    println("muliplier(2) value = " + multiplier(2))
  }

  //  在 multiplier 中有两个变量：i 和 factor。其中的一个 i  是函数的形式参数，在 multiplier 函数被调用时，i 被赋予一个新的值
  //  这样定义的函数变量 multiplier 成为一个"闭包"，因为它 [引用到函数外面定义的变量]，
  //  定义这个函数的过程是 将这个[自由变量捕获而构成一个封闭的函数]。

  var factor = 3
  val multiplier = (i: Int) => i * factor
}