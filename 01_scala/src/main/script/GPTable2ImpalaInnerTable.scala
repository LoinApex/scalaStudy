package script

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.util.Preconditions
import org.slf4j.LoggerFactory

import scala.reflect.io.Path

/*
 * 描述信息  
 *   
 * Date 2019/9/29
 * Version 1.0  
 */
class GPTable2ImpalaInnerTable(partitions:Int) extends ScriptParser {

  private val log=LoggerFactory.getLogger(classOf[GPTable2ImpalaInnerTable])

  //脚本结束匹配正则
  private val endReg = "location \\('gphdfs://[\\d|\\d+\\.]+:\\d+/datalake/ods/(.+)/gp/(.+)/\\?.+'\\)".r

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
      //首行转换
      case _ if line.indexOf("create writable external table") >= 0 => {
        val word = line.split(" ").toBuffer
        s"create table ???${word.last}${System.getProperty("line.separator")}"
      }

      case _ if line.toLowerCase.indexOf("location ('gphdfs://") >= 0 => {    //结束行转换
        val matcher = endReg.pattern.matcher(line.toLowerCase)
        var domain:String =null
        var tableName: String = null
        if (matcher.find()) {
          domain = matcher.group(1)
          tableName = matcher.group(2)
        }
        if (tableName == null) {
          log.error(s"line:${line}")
          throw new Exception("正则匹配表名出错")
        }
        s"partition by hash partitions ${partitions} stored as kudu tblproperties ('kudu.table_name' = 'ods_${domain}.???${tableName}');${System.getProperty("line.separator")}"
      }

      //中间字段行转换
      case _ if line.matches("^.+ .+") => {
        val column = line.split(" ")
        var columnName = column(0) //列名
        var columnType = column(1) //列类型

        if(columnType.endsWith(",")){
          columnType= columnType.substring(0,columnType.length-1)
        }

        if(columnName.contains("$")){
          columnName=columnName.replace("$","_")
        }

        columnType = columnType match {
          case _ if columnType.startsWith("character") => "string"
          case _ if columnType.startsWith("integer") => "bigint"
          case _ if columnType.startsWith("numeric") => {
            val matcher = columnTypeReg.pattern.matcher(columnType)
            if (matcher.find()) {
              val precision = matcher.group(1) //精度
              if (precision.toInt > 0) "double" else "bigint"
            } else {
              "double"
            }
          }
          case _ => columnType
        }

        s"${columnName} ${columnType},${System.getProperty("line.separator")}"
      }
      //字段结束时添加额外字段
      case _ if line.startsWith(")") => {
        s"""
           |bi_pos string,
           |bi_op_ts timestamp,
           |primary key(bi_sfdm,???)
           |)""".stripMargin
      }

      case _ => line

    }

  }
}



/**
  * main方法传参，例：
  * --in C:\\Users\\Qing\\Desktop\\新建文件夹 --out C:\\Users\\Qing\\Desktop\\新建文件夹2
  */
object GPTable2ImpalaInnerTable {

  private val lineSeparator = System.getProperty("line.separator")

  def main(args: Array[String]): Unit = {

    val tool = ParameterTool.fromArgs(args)
    val in = tool.get("in")
    val out = tool.get("out")
    val partitions=tool.get("partitions")
    Preconditions.checkNotNull(in, "缺失参数：--in")
    Preconditions.checkNotNull(in, "缺失参数：--out")
    Preconditions.checkNotNull(partitions, "缺失参数：--partitions")



    new File(out).mkdirs()
    val file = new File(s"${out}/impala_inner_${new SimpleDateFormat("yyyyMMddHHmm").format(new Date())}.sql")
    if (!file.exists()) {
      file.createNewFile()
    }
    val writer = new PrintWriter(file)
    if (Path(in).isDirectory) {  //输入路径是文件夹，则处理文件夹内所有文件
      val iter = new GPTable2ImpalaInnerTable(partitions.toInt).readDirectory(in)
      iter.foreach(item => {
        writer.write(item.replace(s",${lineSeparator})", s"${lineSeparator})"))
        writer.write(lineSeparator)
      })
    } else { //输入路径是文件，则直接处理该文件文件
      val content = new GPTable2ImpalaInnerTable(partitions.toInt).readFile(in)
      writer.write(content.replace(s",${lineSeparator})", s"${lineSeparator})"))
    }

    writer.close()

    println("game is over!")
  }
}