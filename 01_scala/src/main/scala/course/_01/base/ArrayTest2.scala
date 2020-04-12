package course._01.base

import scala.collection.mutable.ArrayBuffer

object ArrayTest2 {

  def main(args: Array[String]) {
    /**
     * 数组
     * Int类型初始化为0 String初始化为null
     */

    //定长
    val aa = new Array[Int](10)

    //直接复制，类型相同
    val bb = Array(1, 2)


    val cc = new ArrayBuffer[Int]()

    cc += 1;
    cc += (2, 3);

    //尾部增加T类型元素
    cc ++= Array(6, 3);

    //移除尾部元素  个数
    cc.trimEnd(1)

    //定点插入元素
    cc.insert(2, 3, 33);



    //移除第三个元素后的一个元素
    for (ii <- cc) println(ii)
    //cc.remove(3)

    //移除第三个元素后的  两个元素
    cc.remove(3, 2)


    for (ii <- cc) println("<><><>" + ii)

    val dd = cc.toArray //变长数组cc 转换为定长数组
    val ee = dd.toBuffer //定长数组转为  变长数组

    for (d <- dd) println("<>/////<>" + d + "    " + dd.length)

    for (ii <- ee) println("<>.......<>" + ii) //数组连续可以遍历


    for (ii <- 0 to ee.length - 1) println("<>.===.<>" + ee(ii)) //数组不连续需要遍历下标  ee(0)  ee(2)


    /* val mm = new ArrayBuffer[Int](2).toBuffer
      mm(1) = 1
     mm(2) = 33


     for(ii <- mm)println("<>...mm....<>"+ii)  //数组连续可以遍历


     //for(ii <-  0 to  mm.length -1 )println("<>.=mm==.<>"+ee(ii))  //数组不连续需要遍历下标
     */

    val A = new Array[Int](3)
    A(0) = 2
    A(2) = 57
    val B = A.toBuffer
    //B.remove(2,2)
    //	for(i <- 1 to (B.length)) print(B(i)+ " " )
    for (i <- 0 to (A.length - 1)) print(A(i) + "--")
    println()
    for (i <- A) print(i + "-<>-")
    println()

    //B(10)=66
    //for(i <- B ) print(i  + "-<B>-" )
    //	for(i <- 0 to (B.length-1)) print(B(i)+ " " )


    /**
     * 推荐使用数组遍历方式
     */
    var intArrayVar = ArrayBuffer(1, 1, 2)
    for (i <- intArrayVar) println("Array Element: " + i)


  }


}
