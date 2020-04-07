package feng

class C5 {
  pointtooaa =>

  private val value1 = "Hello"
  val value2 = "world"

  def add() {
    println(value1 + value2)
  }

  def plus(m: Char) = {
    value2 + m

  }

  //声明内部类，可以调用外部类  .this 或者 指针实现
  class HelloWrold {
    val cc = C5.this.value1
    val cc2 = pointtooaa.value2


  }


}