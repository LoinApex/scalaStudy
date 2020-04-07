package scala.feng

object T4 {
  def main(args: Array[String]) {

    /*
     * 映射：
     * 包含 一组 键值对 元素的集合
     * 只是一个对应的查询，类似于指针
     */

    val Z = Map(1 -> 2, 5 -> 4, 3 -> 5)

    val a = Z(1)
    println(a)

    //检查是否有某个键

    val c = Z.contains(1) //boolean类型
    println(c)

    val d = Z.getOrElse(1, 0) //找到返回1  没有返回0


    //变长
    var W = Z + (3 -> 3)

    W += (5 -> 4, 7 -> 4) //增加映射

    W -= 3 //较少 直接使用  键值


    println(W.contains(3))

    //  映射的枚举 
    for ((i, j) <- Z) print(i, j) // 操作
    // 互换映射的键值
    for ((i, j) <- Z) yield (j, i)
    // 获取映射内键的集合或值的集合 
    val e = Z.keySet
    val f = Z.values
    // 可以利用for表达式只枚举映射的键或者集合

    /**
     * 元祖  
     */
    val on = Array('a', 'b', 'c')
    val wo = Array(1, 2, 3)

    //生成数组

    //  val thr = on.zip(wo)
    val thr = on zip wo


    val fou = on.zip(wo).toMap


    /*  {……}号之间声明的量只有在{}内是有意义的
      {val a=0... {...val b=a}...}// 内层能调用外层声明的量
      {val a=b...{val b=0...}...} //外层不能调用内层声明的量
      内层声明与外层声明相同时，内层使用的是内层的声明，外层使用的是外层的声明
      如：
       */
    val x = 2
    for (i <- 1 to 4) print(x + i)

  }


}