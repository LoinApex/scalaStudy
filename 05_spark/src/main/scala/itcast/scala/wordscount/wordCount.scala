package itcast.scala.wordscount

import scala.io.Source

object wordCount {

  def main(args: Array[String]): Unit = {

    val line = Source.fromFile("D:\\workspaceEeclipseStudy\\testScalaFeng\\src\\itcast\\aa.txt").getLines()
    val tolist = line.toList
    println("tolist     " + tolist)
    val map = tolist.map(x => (x, 1)) //映射成一个tuple
    println("map     " + map)
    val groupby = map.groupBy(x => x._1) //按tuple的key进行分组
    println("groupby      " + groupby)
    val mv = groupby.mapValues(i => i.map(t => t._2).reduce((x, y) => x + y))
    println("mv   reduce   " + mv)
    println("------------------")
    val list = mv.foreach(print)
    //    println("list   " + list)
    println("-------------========-----")

    println(tolist.map { x => (x, 1, 3) })
  }

}