package script

import java.io.{File, PrintWriter, StringReader}
import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.util.Preconditions

import scala.io.Source
import scala.reflect.io.Path

/*
 * 描述信息  
 *   
 * Date 2019/9/29
 * Version 1.0  
 * oracle sql建表语句生成hive 外部表建表语句
 */
class OracleTable2HiveExtTable(partitions:Int) extends ScriptParser {



  //每行字段类型匹配正则
  private val numberTypeReg = "number\\(\\d+,(\\d+)\\)".r
  private val numericTypeReg = "numeric\\(\\d+,(\\d+)\\)".r

  var primaryKey:String =""
  var tableName:String =null
  var hdfsTableName:String = ""
  var domain="yx2"
  var dbName="csg_ods_yx_ex"
  var format="PARQUET"


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
        if(tableName.indexOf("(")>=0) {
          tableName = tableName.substring(0, tableName.length - 1)
        }
        hdfsTableName+=s"${tableName.split("\\.")(1)}".toUpperCase
        tableName=tableName.split("\\.")(1)


        if (tableName == null) {
          throw new Exception("正则匹配表名出错")
        }

        s"""|drop table if exists ${dbName}.${tableName};
            |CREATE external TABLE IF NOT EXISTS ${dbName}.${tableName} (
            |bi_sfdm string,
         """.stripMargin

      }
      case _ if line.indexOf(");") >= 0 => {    //结束行转换
        /*if(primaryKey==""){
          throw new NoPrimaryKeyException(s"没有主键列，表名：${tableName}")
        }*/

        var str=""
        if(format!="TEXTFILE"){
          str+=
            s"""
               |,
               |bi_pos string,
               |bi_op_ts timestamp,
               |bi_current_ts timestamp,
               |bi_up_ts timestamp,
               |bi_op_type string
             """.stripMargin
        }


     /*   str +=
          s"""
             |)
             |ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
             |WITH SERDEPROPERTIES ('field.delim'='001','serialization.encoding'='GB18030')
             |STORED AS ${format}
             |location 'hdfs://nameservice1/DataLake/ods/csg/initdata/${domain}/${hdfsTableName}';
             |
             |
             |"""
            .stripMargin
*/

        //location 'hdfs://nameservice1/DataLake/ods/csg/initdata/${domain}/${hdfsTableName}';
        //ROW FORMAT DELIMITED FIELDS TERMINATED BY '001'
        str +=
          s"""
             |)
             |STORED AS ${format};
             |
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
            val matcher = numberTypeReg.pattern.matcher(columnType)
            if (matcher.find()) {
              val precision = matcher.group(1) //精度
              if (precision.toInt > 0) "double" else "bigint"
            } else {
              "bigint"
            }
          }
          case _ if columnType.startsWith("numeric") =>{
            val matcher = numericTypeReg.pattern.matcher(columnType)
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

       var str= s"${columnName} ${columnType}"

        if(line.endsWith(",")) {
          str += ","
        }

        str+=System.getProperty("line.separator")
        str
      }
      case _ => line

    }

  }
}



object OracleTable2HiveExtTable {

  private val lineSeparator = System.getProperty("line.separator")

  def main(args: Array[String]): Unit = {

    val tool = ParameterTool.fromArgs(args)
    var in = tool.get("in")
    var out = tool.get("out")
    var partitions=tool.get("partitions")

    in="D:\\aaa\\all"
    out ="D:\\aaa\\output\\NOKUDU"
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



    val parquetMap2 = new Properties()
    var parquetPath2 = this.getClass.getResourceAsStream("/scripts/table_4680.properties")
    parquetMap2.load(parquetPath2)

    import scala.collection.JavaConverters._
    for(key<-parquetMap.asScala.keySet) {
      val v=parquetMap2.getProperty(key)
      if(v==null){
        println(s"没有找到的表：$key")
      }else {
        parquetMap.setProperty(key, v)
      }
    }


    var tableCount=0
    new File(out).mkdirs()
    val file = new File(s"${out}/hive_parquet_${new SimpleDateFormat("yyyyMMddHHmm").format(new Date())}.sql")
    if (!file.exists()) {
      file.createNewFile()
    }
    val writer = new PrintWriter(file)
    if (Path(in).isDirectory) {  //输入路径是文件夹，则处理文件夹内所有文件
      new File(in).listFiles().map(item=>{

        var itemName=item.getName
        var newName:String=null
        val suffix= itemName.substring(itemName.lastIndexOf("_"),itemName.length)
        if(suffix.startsWith("_2020")){
          newName=itemName.substring(0,itemName.lastIndexOf("_"))
        }else {
          newName = itemName.substring(0, itemName.lastIndexOf("."))
        }


        if(parquetMap.size>0 && parquetMap.containsKey(newName) && parquetMap.getProperty(newName).toInt == -1) {
          val content = new OracleTable2HiveExtTable(partitions.toInt).readFile(item)
          writer.write(content)
          writer.flush()
          tableCount=tableCount+1
        }/*else{
          val content = new OracleTable2HiveExtTable(partitions.toInt).readFile(item)
          writer.write(content)
          writer.flush()
        }*/
      })

    } else { //输入路径是文件，则直接处理该文件文件
      val content = new OracleTable2HiveExtTable(partitions.toInt).readFile(in)
      writer.write(content)
      writer.flush()
    }

    writer.close()

    println(s"处理表数量：${tableCount}")

    println("game is over!")
  }
}







