package course._01.base

import org.junit.Test

import scala.collection.mutable.ArrayBuffer

/**
 * scala特殊符号使用
 * https://www.zybuluo.com/boothsun/note/1014438
 */
object SymbolTest {

  def main(args: Array[String]): Unit = {
    /*
        =>（匿名函数）
        参考文档：scala => 用法 匿名函数
        => 匿名函数，在Spark中函数也是一个对象可以赋值给一个变量。

        Spark的匿名函数定义格式：
        (形参列表) => {函数体} 所以，=>的作用就是创建一个匿名函数实例。

        比如：(x:Int) => x +1 ，就等同于下面的Java方法：
        public int function(int x) {
          return x+1;
        }
        */

    class Symbol {
      var add = (x: Int) => x + 1
    }

    /* <- （集合遍历）
     循环遍历，示例如下：*/

    var list = Array(1, 2, 3, 4)
    for (aa <- list) {
      printf(aa + "   ")
    }

    /*上面代码类似于Java的代码:
      int[] list = {1,2,3,4};
      for(int aa : list) {
        System.out.print(aa+"   ");
      }
    */


    println("---------")
    /*++=（字符串拼接）*/
    var s: String = "a"
    s += "b"
    println(s)
    s ++= "c"
    println(s)

    /*
    :::三个冒号运算符与::两个冒号运算符
    :::三个冒号运算符表示List的连接操作。(类似于Java中的 list1.addAll(list2))
    ::两个冒号运算符表示普通元素与list的连接操作。(类似于Java中的list1.add(A)操作)
  */
    val one = List(1, 2, 3)
    val two = List(4, 5, 6)
    val three = one ::: two
    println(three.toString())
    val four = 7 :: three
    println(four.toString())



    /*  -> 构造元组和_N访问元组第N个元素
        scala中元组含义：

      元组是不同类型的值聚集线程的列表,通过将多个值使用小括号括起来，即表示元组
      scala中元组与数组区别：数组中元素 数据类型必须一样，但是元组数据类型可以不同。
    */

    val first1 = (1, 2, 3) // 定义三元元组
    val one1 = 1
    val two1 = 2
    val three1 = one1 -> two1
    println(three1) // 构造二元元组
    println(three1._2) // 访问二元元组中第二个值


    /*
    _（下划线）的用法 通配符
    可以起到类似于*作用的通配符
    指代集合中的每一个元素,例如:遍历集合筛选列表中大于某个值的元素。
  */
    val lst = List(1, 2, 3, 4, 5)
    val lstFilter = lst.filter(_ > 3)
    //获取元组中指定下标的元素值
    val ss = (1, "22", "333")
    println(ss._1)

    // 使用模式匹配可以用来获取元组的组员
    val m = Map(1 -> 2, 2 -> 4)
    for ((k, _) <- m) println(k) //如果不需要所有部件， 则在不需要的部件使用_； 本例只取key,因此在value处用_
    // 成员变量而非局部变量添加默认值

    /*
      ：_* 作为一个整体，告诉编译器你希望将某个参数当做数序列处理
      main..查看网页
    */


    println("---------")


    /*    += 为可变数组添加元素
     */

    val arrBuf1 = new ArrayBuffer[Int]()
    arrBuf1 += 11 // 添加一个元素
    println(arrBuf1)


    /*
        -= 从map后者可变数组中移除相应的值
    */
    val arrBuf2 = new ArrayBuffer[Int]()
    arrBuf1 += 11 // 添加一个元素
    arrBuf1 += 12 // 添加一个元素
    arrBuf1 -= 12 // 删除一个元素
    println(arrBuf2)

    var map = Map(1 -> 1, 2 -> 2, 3 -> 3)
    map -= 1
    println(map)


  }

}
