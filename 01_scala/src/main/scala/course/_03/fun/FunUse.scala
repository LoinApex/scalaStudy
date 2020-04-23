package course._03.fun

import org.junit.Test

/**
 *
 */
class FunUse {
  @Test
  def t1: Unit = {
    val y = 0
    (x: Int) => println(x + y)

  }



  /**
   * 函数作为参数传递
   * 让程序动态化，使加工数据的逻辑可以随参数传入
   */
  @Test
  def t2: Unit = {
    val list = List(1, 2, 3)
    println(f1(list, (a, b, c) => a + b + c))
    println(f1(list, _ + _ + _)) //简化方式
    println(f1(list, _ * _ * _))

    def f1(list: List[Int], fn: (Int, Int, Int) => Int) = {
      fn(list(0), list(1), list(2)) //实际就是种回调
    }

  }

  val list = List(1, 2, 3, 4, 5)

  /**
   * 传入函数为泛型方式
   */
  @Test
  def t3: Unit = {
    print(f2(list, (a: Int, b: Int, c: Int) => a * b * c))

    //泛型
    def f2[T](list: List[T], fn: (T, T, T) => T) = {
      fn(list(0), list(1), list(2))
    }


  }

  /**
   * 函数作为返回值
   */
  @Test
  def t4: Unit = {
    println(f3(list)(_ + _ + _))
    println(f3(list)(_ * _ * _))

    def f3[T](list: List[T]) = (fn: (T, T, T) => T) => {
      fn(list(0), list(1), list(2))
    }

  }


  /**
   * 一个函数后面又是一个函数。
   * scala里面的一种特殊语法，叫做 柯里化(Currying，以逻辑学家Haskell Brooks Curry的名字命名)
   * 指的是将原来接收两个参数的函数变成接受一个参数的函数的过程,新的函数返回一个以原有第二个参数作为参数的函数。
   * 函数的定义结构可以利于函数的顺序执行; 使用科里化将一个大的计算分解为几个小的计算过程
   */
  @Test
  def t5: Unit = {
    //    调用的语法：方法名（实参1）（实参2）
    f4(list)(_ + _ + _)

    //定义的语法：def 方法名(参数1：类型)(参数2：类型)
    def f4[T](list: List[T])(fn: (T, T, T) => T) = {
      fn(list(0), list(1), list(2))
    }

  }

}
