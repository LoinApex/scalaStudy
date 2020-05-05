package script

import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.{Date, Properties}

import org.apache.commons.io.FileUtils
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.util.Preconditions

import scala.collection.mutable
import scala.reflect.io.Path


class NoPrimaryKeyException(errorMsg:String) extends RuntimeException(errorMsg)
class ColumnTypeKeyException(errorMsg:String) extends RuntimeException(errorMsg)

/*
 * 描述信息  
 *   
 * Date 2019/9/29
 * Version 1.0  
 * oracle sql建表语句生成 impala 内部表建表语句
 */
class OracleTable2ImpalaInnerTable(partitions:Int) extends ScriptParser {


  //每行字段类型匹配正则
  private val numberTypeReg = "number\\(\\d+,(\\d+)\\)".r
  private val numericTypeReg = "numeric\\(\\d+,(\\d+)\\)".r

  var primaryKey =new mutable.HashMap[String,String]()
  var tableName:String =null
  var kuduDbName="csg_ods_yx"

  var map=new mutable.ListMap[String,Int]()
  map.put("date",0)
  map.put("number",0)
  map.put("varchar2",0)

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
        tableName=line.split("\\.")(1)
        tableName=tableName.substring(0,tableName.length-1)
        //tableName+="_tmp"
        if (tableName == null) {
          throw new Exception("正则匹配表名出错")
        }


        //s"drop table ${kuduDbName}.${kuduTableName}"

        s"""
           |drop table ${kuduDbName}.${tableName};
           |create table if not exists csg_ods_yx.${tableName} (
           |(primary-Key),
           |bi_sfdm string,
           |""".stripMargin
      }
      case _ if line.indexOf(");") >= 0 => {    //结束行转换
        if(primaryKey.size<=0){
          throw new NoPrimaryKeyException(s"没有主键列，表名：${tableName}")
        }
        /*if(primaryKey.size>1){
          throw new CombinedKeyException(s"复合主键，表名：${tableName}")
        }*/

        var str =
          s"""
             |bi_pos string,
             |bi_op_ts timestamp,
             |bi_current_ts timestamp,
             |bi_up_ts timestamp,
             |bi_op_type string,
             |primary key( ${primaryKey.keySet.mkString(",")}, bi_sfdm )
             |)
             |partition by hash partitions ${partitions} stored as kudu
             |tblproperties ('kudu.table_name' = 'csg_ods_yx.${tableName}');
             |
             |
             |"""
            .stripMargin


        var content= this.getParseContent().replace("(primary-Key)",primaryKey.map(item=>{
          s"${item._1} ${item._2}"
        }).mkString(","))

        this.setParseContent(content)

        println(tableName.toUpperCase,map.toSeq.sortBy(_._1))

        tableName=""
        primaryKey.clear()



        str
      }
      case _ if line.matches(".+ .+") => {    //中间字段行转换
        val column = line.split(" ")
        val columnName = column(0) //列名
        var columnType = column(1) //列类型

        var newColumnType:String=null
        if(columnType.lastIndexOf("(")>0){
           newColumnType=columnType.substring(0,columnType.lastIndexOf("("))
        }else{
          newColumnType=columnType
        }

        newColumnType=newColumnType.replace(",","")

        if(!map.contains(newColumnType)) {
          map += (newColumnType -> 1)
        }else {
          map += (newColumnType -> (map.get(newColumnType).get + 1))
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
          case _ if columnType.startsWith("clob") || columnType.startsWith("blob") => throw new ColumnTypeKeyException(s"存在clob或blob字段，表名${tableName}")
          case _ => columnType
        }

        if(line.indexOf("primary key")>0) {
          primaryKey.put(columnName,columnType)
          ""
        }else {
          s"${columnName} ${columnType},${System.getProperty("line.separator")}"
        }
      }
      case _ => line

    }

  }
}



object OracleTable2ImpalaInnerTable {

  private val lineSeparator = System.getProperty("line.separator")

  def main(args: Array[String]): Unit = {

    val tool = ParameterTool.fromArgs(args)
    var in = tool.get("in")
    var out = tool.get("out")
    var noKey = tool.get("no-key")
    var errorType = tool.get("error-type")
    var partitions = tool.get("partitions")

    in = "D:\\aaa\\111"
    out = "D:\\aaa\\output\\111"
    noKey = s"${out}/nokey"
    errorType = s"${out}/blobtype"
    partitions = "10"

    Preconditions.checkNotNull(in, "缺失参数：--in")
    Preconditions.checkNotNull(in, "缺失参数：--out")
    Preconditions.checkNotNull(partitions, "缺失参数：--partitions")


    val parquetMap = new Properties()
    var parquetPath = this.getClass.getResourceAsStream("/scripts/hive-parquet.properties")
    parquetMap.load(parquetPath)


  /*  val parquetMap2 = new Properties()
    var parquetPath2 = this.getClass.getResourceAsStream("/scripts/table_4680.properties")
    parquetMap2.load(parquetPath2)

    for(key<-parquetMap.asScala.keySet) {
      val v=parquetMap2.getProperty(key)
      if(v==null){
        println(s"没有找到的表：$key")
      }else {
        parquetMap.setProperty(key, v)
      }
    }*/


 /*   val source = Source.fromFile(parquetPath.toURI)
    val parquetMap = source.getLines().map(item => {
      var tableName = item.replace(".", "_").toUpperCase
      tableName -> tableName
    }).toMap*/


    var successTableCount = 0
    var noPrimaryKeyCount = 0
    var errorTypeCount = 0
    new File(out).mkdirs()
    val file = new File(s"${out}/impala_inner_${new SimpleDateFormat("yyyyMMddHHmm").format(new Date())}.sql")
    if (!file.exists()) {
      file.createNewFile()
    }
    val writer = new PrintWriter(file)
    if (Path(in).isDirectory) { //输入路径是文件夹，则处理文件夹内所有文件
      new File(in).listFiles().foreach(item => {

        var itemName=item.getName
        var newName:String=null
        val suffix= itemName.substring(itemName.lastIndexOf("_"),itemName.length)
        if(suffix.startsWith("_2020")){
          newName=itemName.substring(0,itemName.lastIndexOf("_"))
        }else {
          newName = itemName.substring(0, itemName.lastIndexOf("."))
        }

        parquetMap.clear()
        if(parquetMap.size>0 && parquetMap.containsKey(newName) /*&& parquetMap.getProperty(newName).toInt>0*/) {

          try {
            val content = new OracleTable2ImpalaInnerTable(parquetMap.getProperty(newName).toInt).readFile(item.getPath)
            writer.write(content)
            writer.write(lineSeparator)
            writer.flush()
            successTableCount = successTableCount + 1
          } catch {
            case e: NoPrimaryKeyException => {
              println(e)
              var targetPath = s"${noKey}/${item.getName}"
              //Files.move(Paths.get(item.getPath),Paths.get((targetPath)))
              FileUtils.copyFile(new File(item.getPath), new File(targetPath))
              noPrimaryKeyCount = noPrimaryKeyCount + 1
            }
            case ex: ColumnTypeKeyException => {
              println(ex)
              var targetPath = s"${errorType}/${item.getName}"
              FileUtils.copyFile(new File(item.getPath), new File(targetPath))
              errorTypeCount = errorTypeCount + 1
            }
            case e: Exception => println(e)
          }

        }else{
          try {
            val content = new OracleTable2ImpalaInnerTable(2).readFile(item.getPath)
            writer.write(content)
            writer.write(lineSeparator)
            writer.flush()
            successTableCount = successTableCount + 1
          } catch {
            case e: NoPrimaryKeyException => {
              println(e)
              var targetPath = s"${noKey}/${item.getName}"
              //Files.move(Paths.get(item.getPath),Paths.get((targetPath)))
              FileUtils.copyFile(new File(item.getPath), new File(targetPath))
              noPrimaryKeyCount = noPrimaryKeyCount + 1
            }
            case ex: ColumnTypeKeyException => {
              println(ex)
              var targetPath = s"${errorType}/${item.getName}"
              FileUtils.copyFile(new File(item.getPath), new File(targetPath))
              errorTypeCount = errorTypeCount + 1
            }
            case e: Exception => println(e)
          }
        }

      })

      println(s"成功的表数量:${successTableCount}")
      println(s"没有主键的表数量:${noPrimaryKeyCount}")
      println(s"错误字段类型的表数量：${errorTypeCount}")
      println(s"总表数量：${successTableCount + noPrimaryKeyCount + errorTypeCount}")

    } else { //输入路径是文件，则直接处理该文件文件
      val content = new OracleTable2ImpalaInnerTable(partitions.toInt).readFile(in)
      writer.write(content)
    }

    writer.close()

    println("game is over!")
  }





}



