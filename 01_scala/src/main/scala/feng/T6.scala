package scala.feng

object T6 {

  /**
   * 可变list测试
   */
  def main(args: Array[String]): Unit = {

    import scala.collection.mutable.ListBuffer

    /**
      * Created by toto on 2017/6/28.
      */
    //构建一个可变列表，初始有3个元素，1,2,3
    val lst0 = ListBuffer[Int](1, 2, 3)
    println(lst0)

    //创建一个空的可变列表
    val lst1 = new ListBuffer[Int]
    //向lst1中追加元素，注意：没有生成新的集合
    lst1 += 4
    println(lst1)
    lst1.append(5)
    println(lst1)

    //将lst1中的元素最近到lst0中，注意：没有生成新的集合
    lst0 ++= lst1
    println(lst0)

    //将lst0 和 lst1合并成一个新的ListBuffer，注意：生成一个集合
    val lst2 = lst0 ++ lst1
    println(lst2)

    //将元素追加到lst0的后面生成一个新的集合
    val lst3 = lst0 :+ 5
    println(lst3)
  }
}



