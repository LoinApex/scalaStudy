package script

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.util.Preconditions

import scala.reflect.io.Path

/*
 * 描述信息  
 *   
 * Date 2019/9/29
 * Version 1.0  
 */
class HiveSql2ImpalaSql(partitions:Int) extends ScriptParser {


  //每行字段类型匹配正则
  private val columnTypeReg = "number\\(\\d+,(\\d+)\\)".r

  var primaryKey:String =""
  var tableName:String =null
  var hdfsTableName:String = ""


  override def isBeginParse(line: String): Boolean = {
    line.toLowerCase.startsWith("create table")
  }

  override def isEndParse(line: String): Boolean = {
    line.toLowerCase.startsWith(")")
  }

  override def lineParse(statement: String): String = {
    val line = statement.toLowerCase


    return line match {
      case _ if line.indexOf("create table ") >= 0 => {  //首行转换
        tableName=line.split(" ")(2)
        tableName=tableName.substring(0,tableName.length-1)
        hdfsTableName+=s"${tableName.replace(".","_")}".toUpperCase
        tableName=tableName.split("\\.")(1)


        if (tableName == null) {
          throw new Exception("正则匹配表名出错")
        }

        s"""${tableName} | insert into csg_ods_yx.${tableName} select '03',""".stripMargin
      }
      case _ if line.indexOf(");") >= 0 => {    //结束行转换
       /* if(primaryKey==""){
          throw new RuntimeException("没有主键列。。。")
        }*/

        var str =
          s"""'',now(),now(),now(),'' from csg_ods_yx_ex.${tableName};
             |"""
            .stripMargin

        tableName=""
        primaryKey=""
        hdfsTableName=null

        str
      }
      case _ if line.matches(".+ .+") => {    //中间字段行转换
        val column = line.split(" ")
        val columnName = column(0) //列名
        var columnType = column(1) //列类型
        if(line.indexOf("primary key")>0) {
          primaryKey += columnName + ","
        }

        columnType = columnType match {
          case _ if columnType.startsWith("varchar2") ||  columnType.startsWith("nvarchar2") || columnType.startsWith("char")=> "string"
          case _ if columnType.startsWith("number") => {
            val matcher = columnTypeReg.pattern.matcher(columnType)
            if (matcher.find()) {
              val precision = matcher.group(1) //精度
              if (precision.toInt > 0) "double" else "bigint"
            } else {
              "bigint"
            }
          }
          case _ if columnType.startsWith("date") =>"timestamp"
          case _ => columnType
        }

       var str= s"${columnName},"
        str
      }
      case _ => line

    }

  }
}


object HiveSql2ImpalaSql {

  private val lineSeparator = System.getProperty("line.separator")

  def main(args: Array[String]): Unit = {

    val tool = ParameterTool.fromArgs(args)
    var in = tool.get("in")
    var out = tool.get("out")
    var partitions=tool.get("partitions")

    in="D:\\aaa\\yx_4_5_6"
    out ="D:\\aaa\\output\\yx_4_5_6"
    partitions="10"

    Preconditions.checkNotNull(in, "缺失参数：--in")
    Preconditions.checkNotNull(in, "缺失参数：--out")
    Preconditions.checkNotNull(partitions, "缺失参数：--partitions")

    val parquetMap = new Properties()
    var parquetPath=this.getClass.getResourceAsStream("/scripts/hive-parquet.properties")
    parquetMap.load(parquetPath)

    /*val source = Source.fromFile(parquetPath.toURI)
    val parquetMap=source.getLines().map(item=>{
      var tableName=item.replace(".","_").toUpperCase()
      tableName->tableName
    }).toMap*/

    var tableCount=0
    new File(out).mkdirs()
    val file = new File(s"${out}/hive_impala_sql_${new SimpleDateFormat("yyyyMMddHHmm").format(new Date())}.sql")
    if (!file.exists()) {
      file.createNewFile()
    }
    val writer = new PrintWriter(file)
    if (Path(in).isDirectory) {  //输入路径是文件夹，则处理文件夹内所有文件

      new File(in).listFiles().map(item=>{
        var itemName=item.getName
        var newName=itemName.substring(0,itemName.lastIndexOf("."))

        if(parquetMap.size>0 && parquetMap.containsKey(newName)) {
          val content = new HiveSql2ImpalaSql(partitions.toInt).readFile(item)
          writer.write(content)
          writer.flush()
          tableCount=tableCount+1
        }
      })


    } else { //输入路径是文件，则直接处理该文件文件
      val content = new HiveSql2ImpalaSql(partitions.toInt).readFile(in)
      writer.write(content.replace(s",${lineSeparator})", s"${lineSeparator})"))
      writer.flush()
    }

    writer.close()

    println(s"处理表数量：${tableCount}")

    println("game is over!")
  }
}








