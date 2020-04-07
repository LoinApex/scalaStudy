package scala.feng

object T1 {
  /**
    * 赋值
    */
  def main(args: Array[String]) {

    var myVar: String = "Foo" //变量
    val myVal: String = "Foo" //不可变变量
    val (myVar1, myVar2) = Pair(40, "Foo")

    println(myVar1);

    var a = 10
    var b = 20

    println(a==b)



  }

}