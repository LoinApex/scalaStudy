package script

import java.io._
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.util.Preconditions

import scala.reflect.io.Path

/*
 * 描述信息  
 *   
 * Date 2019/9/29
 * Version 1.0  
 *
 *
 */
class GPTable2ImpalaExtTable extends ScriptParser {

  //脚本结束匹配正则
  private val endReg = "location \\('gphdfs://[\\d|\\d+\\.]+:\\d+/datalake/ods/yx/gp/(.+)/'\\)".r

  //每行字段类型匹配正则
  private val columnTypeReg = "numeric\\(\\d+,(\\d+)\\)".r

  override def isBeginParse(line: String): Boolean = {
    line.toLowerCase.startsWith("create writable external table")
  }

  override def isEndParse(line: String): Boolean = {
    line.toLowerCase.startsWith("location ('gphdfs://")
  }

  override def lineParse(statement: String): String = {
    val line = statement.toLowerCase

    return line match {
      case _ if line.indexOf("create writable external table") >= 0 => { //首行转换
        val word = line.split(" ").toBuffer
        word.remove(1)
        word(word.length - 1) = "ods_yx_gp." + word.last
        word.mkString(" ") + System.getProperty("line.separator")
      }
      case _ if line.indexOf("location ('gphdfs://") >= 0 => { //结束行转换
        val matcher = endReg.pattern.matcher(line)
        var tableName: String = null
        if (matcher.find()) {
          tableName = matcher.group(1)
        }

        if (tableName == null) {
          throw new Exception("正则匹配表名出错")
        }

        val str =
          s"""
             |row format delimited fields terminated by "|"
             |stored as textfile
             |location "/Datalake/ods/yx/gp/${tableName}";
         """.stripMargin
        str
      }
      case _ if line.matches(".+ .+") => { //中间字段行转换
        val column = line.split(" ")
        val columnName = column(0) //列名
        var columnType = column(1) //列类型

        columnType = columnType match {
          case _ if columnType.startsWith("character") => "string"
          case _ if columnType.startsWith("numeric") => {
            val matcher = columnTypeReg.pattern.matcher(columnType)
            if (matcher.find()) {
              val precision = matcher.group(1) //精度
              if (precision.toInt > 0) "double" else "bigint"
            } else {
              "bigint"
            }
          }
          case _ => columnType
        }

        s"${columnName} ${columnType},${System.getProperty("line.separator")}"
      }
      case _ => line

    }

  }
}


/**
 * main方法传参，例：
 * --in C:\\Users\\Qing\\Desktop\\新建文件夹 --out C:\\Users\\Qing\\Desktop\\新建文件夹2
 */
object GPTable2ImpalaExtTable {

  private val lineSeparator = System.getProperty("line.separator")

  def main(args: Array[String]): Unit = {

    val tool = ParameterTool.fromArgs(args)
    val in = tool.get("in")
    val out = tool.get("out")
    Preconditions.checkNotNull(in, "缺失参数：--in")
    Preconditions.checkNotNull(in, "缺失参数：--out")

    new File(out).mkdirs()
    val file = new File(s"${out}/impala_ext_${new SimpleDateFormat("yyyyMMddHHmm").format(new Date())}.sql")
    if (!file.exists()) {
      file.createNewFile()
    }
    val writer = new PrintWriter(file)
    if (Path(in).isDirectory) { //输入路径是文件夹，则处理文件夹内所有文件
      val iter = new GPTable2ImpalaExtTable().readDirectory(in)
      iter.foreach(item => {
        writer.write(item.replace(s",${lineSeparator})", s"${lineSeparator})"))
        writer.write(lineSeparator)
      })
    } else { //输入路径是文件，则直接处理该文件文件
      val content = new GPTable2ImpalaExtTable().readFile(in)
      writer.write(content.replace(s",${lineSeparator})", s"${lineSeparator})"))
    }

    writer.close()

    println("game is over!")
  }
}
