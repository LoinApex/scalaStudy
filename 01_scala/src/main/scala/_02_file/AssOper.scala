package _02_file


import java.io.PrintWriter

import org.apache.commons.io.FileUtils
import org.junit.Test

import scala.io.Source
import scala.util.matching.Regex

/*
统计单词 导入有道单词本
1.读文件,截取英文部分
2.空格分割获取单词,去重,统计词频
 */
object AssOper {


}

class AssOper {

  @Test
  def aab2b: Unit = {
    val numberPattern: Regex = "[a-zA-Z]".r
    val iterator = numberPattern.findAllIn("Dialogue: 0,0:00:20.52,0:00:22.33,Default,NTP,0,0,0,,-比利  -干嘛\\N{\\fs12}{\\b0}{\\c&HFFFFFF&}{\\3c&H2F2F2F&}{\\4c&H000000&}- Billy? - What?")
    iterator.toArray.foreach(println)

  }

  val filepath = "C:\\Users\\FENG\\Desktop\\[zmk.pw]少年谢尔顿.第1季.全22集[6.9]Young.Sheldon.S01.1080p.WEB-DL.DD5.1.H.264-YFN.Sub\\Young.Sheldon.S01E03.Poker.Faith.and.Eggs.1080p.WEB-DL.DD5.1.H.264-YFN.ChsEngA.ass"


  @Test
  def aabb: Unit = {

    for (line <- (Source fromFile(filepath, "UTF-16")).getLines) {

      if (line.contains("H000000")) {
        val str = line.split("H000000") {
          1
        }.replaceAll("&}", "").replaceAll("♪", "").replaceAll("\\?", "")
        println(str)
      }

    }
  }

  def lineno(line: String): Unit = {
    if (line.contains("H000000")) {
      val list = line.split("H000000") {
        1
      }.replaceAll("&}", "").replaceAll("♪", "").replaceAll("\\?", "").split(" ").toList


    }

  }

  def lineDel(line: String, out: PrintWriter): Unit = {
    if (line.contains("H000000")) {
      val list = line.split("H000000") {
        1
      }.replaceAll("&}", "").replaceAll("♪", "").replaceAll("\\?", "").split(" ").toList
      for (li <- list) {
        out.println(li)
      }
    }
  }


  val fileMany = "D:\\ProWork\\aa\\wordMany.txt"


  def lineDelList(line: String): List[String] = {
    if (line.contains("H000000")) {
      line.split("H000000") {
        1
      }.replaceAll("&}", "").replaceAll("♪", "").replaceAll("\\?", "").split(" ").toList
    } else {
      null
    }
  }

  def lineDelListR(line: String,list:List[String]) = {
    if (line.contains("H000000")) {
      val ll = line.split("H000000") {
        1
      }.replaceAll("&}", "").replaceAll("♪", "").replaceAll("\\?", "").split(" ").toList
      list::ll
    } else {
      list
    }
  }

  @Test
  def aa1111: Unit = {
//    val fileout = "D:\\ProWork\\aa\\scala\\assout.txt"
//    val out = new PrintWriter(new File(fileout))
//    val strings = lineDel(lineString, out)
//    out.close()

    //{aa,1}

//    var listr=List[String]
//    val unit = Source.fromFile(filepath, "UTF-16").getLines().foreach(_ ->lineDelListR(_,listr))

     println( )

  }


  @Test
  def aa1: Unit = {

    val list = Source.fromFile("c:/words.txt").getLines().toList
      .map { x => (x, 1) }.groupBy { x => x._1 }
      .mapValues { list => list.map { tuple => tuple._2 }.reduce { (x, y) => x + y } }
      .foreach(x => println(x))

  }

  val lineString = "Dialogue: 0,0:01:38.07,0:01:38.77,Default,NTP,0,0,0,,我真的很担心\\N{\\fs12}{\\b0}{\\c&HFFFFFF&}{\\3c&H2F2F2F&}{\\4c&H000000&}I'm worried.";

  @Test
  def aa: Unit = {
    val str = lineString.split("H000000") {
      1
    }.replaceAll("&}", "")
    println(str)

  }
  @Test
  def a11a: Unit = {

  print("dsfldjflsjd")
  }

}
