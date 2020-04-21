//package _02_file
//
//
//import java.nio.file.Path
//
//import scala.io.Source
//import scala.util.control.Breaks
//
///*
// * 描述信息  
// * User: Qing  
// * Date 2019/9/29
// * Version 1.0  
// */
//abstract class ScriptParser {
//
//
//  private var phase = 0
//
//  private val buf = new StringBuffer()
//
//
//  /**
//   * 读文件夹
//   *
//   * @param path
//   */
//  def readDirectory(path: Path) = {
//    File(path).toDirectory.list.map(this.readFile(_))
//  }
//
//
//  /**
//   * 读文件
//   *
//   * @param path
//   * @return
//   */
//  def readFile(path: Path): String = {
//    if (!path.isFile) {
//      throw new Exception("非文件路径")
//    }
//    val source = Source.fromFile(path.toURI)
//    this.parse(source)
//    //this.parse(source.getLines().toList.mkString(System.getProperty("line.separator")))
//    this.buf.toString()
//  }
//
//
//  /**
//   * 内容转换
//   *
//   * @param source
//   */
//  def parse(source: Source): Unit = {
//    val lines = source.getLines()
//    this.processParse(lines)
//  }
//
//  /**
//   * 内容转换，方法重载
//   *
//   * @param content
//   */
//  def parse(content: String): Unit = {
//    if (content == null || content.length < 0) return
//    val lines = Source.fromString(content).getLines()
//    this.processParse(lines)
//  }
//
//
//  private def processParse(lines: Iterator[String]): Unit = {
//    phase = 0
//    buf.setLength(0)
//    Breaks.breakable {
//      while (lines.hasNext) {
//        var line = lines.next().trim
//
//        phase match {
//          case 0 => {
//            if (isBeginParse(line)) {
//              buf.append(lineParse(line))
//              phase = phase + 1
//            }
//          }
//          case 1 => {
//            buf.append(lineParse(line))
//            if (isEndParse(line)) {
//              phase = phase + 1
//            }
//          }
//        }
//
//        if (phase == 2) {
//          Breaks.break()
//        }
//
//      }
//    }
//  }
//
//  def isParseSuccess(): Boolean = {
//    phase == 2
//  }
//
//  def getParseContent(): String = {
//    this.buf.toString
//  }
//
//  def setParseContent(content: String): Unit = {
//    this.buf.delete(0, this.buf.length())
//    this.buf.append(content)
//  }
//
//  def isBeginParse(line: String): Boolean
//
//  def isEndParse(line: String): Boolean
//
//  def lineParse(line: String): String
//
//
//}
