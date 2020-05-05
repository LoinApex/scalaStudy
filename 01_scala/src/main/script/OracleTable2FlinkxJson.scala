package script

import java.io.{File, PrintWriter}
import java.util.Properties

import org.apache.commons.io.FileUtils
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.util.Preconditions

import scala.reflect.io.Path

/*
 * 描述信息  
 *   
 * Date 2019/9/29
 * Version 1.0  
 * oracle sql建表语句生成Flinkx建表语句
 */
class OracleTable2FlinkxJson(channel:Int) extends ScriptParser {


  //每行字段类型匹配正则
  private val numberTypeReg = "number\\(\\d+,(\\d+)\\)".r
  private val numericTypeReg = "numeric\\(\\d+,(\\d+)\\)".r

  var domain="dameng"
  var kuduDbName="csg_ods_yx"
  var primaryKey:String =""
  var tableName:String =null
  var hdfsTableName:String = ""
  var kuduTableName=""

  var colsIdx=0

  var hdfsContent:String=""
  var kuduContent:String=""

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
        if(tableName.indexOf("(")>=0){
          tableName=tableName.substring(0,tableName.length-1)
        }

        hdfsTableName+=tableName.split("\\.")(1).toUpperCase
        kuduTableName+=tableName.split("\\.")(1)//+"_tmp"


        if (tableName == null) {
          throw new Exception("正则匹配表名出错")
        }

        ""
      }
      case _ if line.indexOf(");") >= 0 => {    //结束行转换
        if(primaryKey==""){
          throw new NoPrimaryKeyException(s"没有主键列，表名：${tableName}")
        }


        hdfsContent=hdfsContent.substring(0,hdfsContent.length-1)
        kuduContent=kuduContent.substring(0,kuduContent.length-1)


        //216配置
        var str=
          s"""
             |{
             |	"job":{
             |		"content":[
             |			{
             |				"reader":{
             |					"parameter":{
             |						"path":"hdfs://nameservice1/DataLake/ods/csg/initdata/${domain}/${hdfsTableName}",
             |						"hadoopConfig":{
             |							"dfs.ha.namenodes.nameservice1":"namenode94,namenode211",
             |							"dfs.namenode.rpc-address.nameservice1.namenode94":"cdh1:8020",
             |							"dfs.namenode.rpc-address.nameservice1.namenode211":"cdh2:8020",
             |							"dfs.nameservices":"nameservice1",
             |							"dfs.client.failover.proxy.provider.nameservice1":"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
             |						},
             |						"column":[
             |              ${hdfsContent},
             |              {"index":${colsIdx},"name":"bi_sfdm","type":"string","value": "03" }
             |            ],
             |						"defaultFS":"hdfs://nameservice1",
             |						"fieldDelimiter":"\\\\001",
             |						"fileType":"text",
             |            "encoding": "GB18030"
             |					},
             |					"name":"hdfsreader"
             |				},
             |				"writer":{
             |					"parameter":{
             |						"flushMode":"manual_flush",
             |						"bossCount":1,
             |						"masterAddresses":"cdh2:7051,cdh3:7051,cdh5:7051",
             |						"queryTimeout":30000,
             |						"adminOperationTimeout":30000,
             |						"operationTimeout":30000,
             |						"batchInterval":10000,
             |						"column":[
             |              ${kuduContent},
             |              {"name":"bi_sfdm","type":"string"}
             |						],
             |						"batchSizeBytes":2097152,
             |						"writeMode":"insert",
             |						"table":"${kuduDbName}.${kuduTableName}"
             |					},
             |					"name":"kuduwriter"
             |				}
             |			}
             |		],
             |		"setting":{
             |			"dirty":{
             |				"path":"/DataLake/ods/zc/cdc_zb/search/dirty/${hdfsTableName}",
             |				"hadoopConfig":{
             |					"dfs.ha.namenodes.nameservice1":"namenode94,namenode211",
             |					"dfs.namenode.rpc-address.nameservice1.namenode94":"cdh1:8020",
             |					"fs.default.name":"hdfs://nameservice1",
             |					"dfs.namenode.rpc-address.nameservice1.namenode211":"cdh2:8020",
             |					"dfs.nameservices":"nameservice1",
             |					"dfs.client.failover.proxy.provider.nameservice1":"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
             |					"fs.hdfs.impl.disable.cache":"true",
             |					"dfs.ha.automatic-failover.enabled":"true"
             |				}
             |			},
             |			"errorLimit":{
             |				"percentage":20,
             |				"record":10000000
             |			},
             |			"speed":{
             |				"bytes":10485760,
             |				"channel":20
             |			}
             |		}
             |	}
             |}
             |
             |
             |""".stripMargin




        //122配置
        str=
          s"""
             |{
             |	"job":{
             |		"content":[
             |			{
             |				"reader":{
             |					"parameter":{
             |						"path":"hdfs://nameservice1/DataLake/ods/csg/initdata/${domain}/${hdfsTableName}",
             |						"hadoopConfig":{
             |							"dfs.ha.namenodes.nameservice1":"namenode94,namenode211",
             |							"dfs.namenode.rpc-address.nameservice1.namenode94":"cdh1:8020",
             |							"dfs.namenode.rpc-address.nameservice1.namenode211":"cdh2:8020",
             |							"dfs.nameservices":"nameservice1",
             |							"dfs.client.failover.proxy.provider.nameservice1":"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider"
             |						},
             |						"column":[
             |              ${hdfsContent},
             |              {"index":${colsIdx},"name":"bi_sfdm","type":"string","value": "03" }
             |            ],
             |						"defaultFS":"hdfs://nameservice1",
             |						"fieldDelimiter":"\\\\001",
             |						"fileType":"text",
             |            "encoding": "GB18030"
             |					},
             |					"name":"hdfsreader"
             |				},
             |				"writer":{
             |					"parameter":{
             |						"flushMode":"manual_flush",
             |						"bossCount":1,
             |						"masterAddresses":"sjzxhdp02:7051,sjzxhdp01:7051,sjzxhdp03:7051",
             |						"queryTimeout":30000,
             |						"adminOperationTimeout":30000,
             |						"operationTimeout":30000,
             |						"batchInterval":10000,
             |						"column":[
             |              ${kuduContent},
             |              {"name":"bi_sfdm","type":"string"}
             |						],
             |						"batchSizeBytes":2097152,
             |						"writeMode":"insert",
             |						"table":"${kuduDbName}.${kuduTableName}"
             |					},
             |					"name":"kuduwriter"
             |				}
             |			}
             |		],
             |		"setting":{
             |			"dirty":{
             |				"path":"/DataLake/ods/zc/cdc_zb/search/dirty/${hdfsTableName}",
             |				"hadoopConfig":{
             |					"dfs.ha.namenodes.nameservice1":"namenode94,namenode211",
             |					"dfs.namenode.rpc-address.nameservice1.namenode94":"cdh1:8020",
             |					"fs.default.name":"hdfs://nameservice1",
             |					"dfs.namenode.rpc-address.nameservice1.namenode211":"cdh2:8020",
             |					"dfs.nameservices":"nameservice1",
             |					"dfs.client.failover.proxy.provider.nameservice1":"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
             |					"fs.hdfs.impl.disable.cache":"true",
             |					"dfs.ha.automatic-failover.enabled":"true"
             |				}
             |			},
             |			"errorLimit":{
             |				"percentage":20,
             |				"record":10000000
             |			},
             |			"speed":{
             |				"bytes":10485760,
             |				"channel":${channel}
             |			}
             |		}
             |	}
             |}
             |
             |
             |""".stripMargin



        tableName=null
        hdfsTableName=null
        kuduTableName=null
        primaryKey=""
        hdfsContent=""
        kuduContent=""
        colsIdx=0

        str
      }
      case _ if line.matches(".+ .+") => {    //中间字段行转换
        val column = line.split(" ")
        val columnName = column(0).replaceAll("\"","") //列名
        var columnType = column(1) //列类型
        if(line.indexOf("primary key")>0) {
          primaryKey += columnName + ","
        }

        columnType = columnType match {
          case _ if columnType.startsWith("varchar2") ||  columnType.startsWith("nvarchar2") || columnType.startsWith("char")=> "string"
          case _ if columnType.startsWith("number")  => {
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
          case _ if columnType.startsWith("byte") =>"bigint"
          case _ if columnType.startsWith("clob") || columnType.startsWith("blob") => throw new ColumnTypeKeyException(s"存在clob或blob字段，表名${tableName}")
          case _ => columnType
        }


        //******额外生成flinkx对应的字段信息
        var timeFormat=""
        if(columnType=="timestamp"){
          timeFormat=",\"format\":\"yyyy-MM-dd hh:mm:ss\""
        }

        hdfsContent+=
          s"""
             |{"index":${colsIdx},"name":"${columnName}","type":"${columnType}"${timeFormat}},""".stripMargin


        var kuduColumnType=""
        if(columnType=="bigint"){
          kuduColumnType="int64"
        }else if(columnType=="timestamp"){
          kuduColumnType="unixtime_micros"
        }else{
          kuduColumnType=columnType
        }

        kuduContent+=
          s"""
             |{"name":"${columnName}","type":"${kuduColumnType}"},""".stripMargin

        colsIdx=colsIdx+1

        //******************

        ""
      }
      case _ => line

    }

  }
}



object OracleTable2FlinkxJson {

  private val lineSeparator = System.getProperty("line.separator")

  def main(args: Array[String]): Unit = {

    val tool = ParameterTool.fromArgs(args)
    var in = tool.get("in")
    var out = tool.get("out")
    var channel=tool.get("partitions")

    in="D:\\aaa\\dameng"
    out ="D:\\aaa\\output\\dameng\\flinkx"
    channel="20"

    Preconditions.checkNotNull(in, "缺失参数：--in")
    Preconditions.checkNotNull(in, "缺失参数：--out")
    Preconditions.checkNotNull(channel, "缺失参数：--channel")

    val parquetMap = new Properties()
    var parquetPath = this.getClass.getResourceAsStream("/scripts/hive-parquet.properties")
    parquetMap.load(parquetPath)

    var tableCount=0

    new File(out).mkdirs()
    var writer:PrintWriter=null

    if (Path(in).isDirectory) {  //输入路径是文件夹，则处理文件夹内所有文件
      new File(in).listFiles().foreach(item=>{

        var itemName=item.getName
        var newName:String=null
        val suffix= itemName.substring(itemName.lastIndexOf("_"),itemName.length)
        if(suffix.startsWith("_2020")){
          newName=itemName.substring(0,itemName.lastIndexOf("_"))
        }else {
          newName = itemName.substring(0, itemName.lastIndexOf("."))
        }

        parquetMap.clear()
        if(parquetMap.size>0 && parquetMap.containsKey(newName)) {

          try {
            val content = new OracleTable2FlinkxJson(channel.toInt).readFile(item.getPath)
            val file = new File(s"${out}/${item.getName}.json")
            if (!file.exists()) {
              file.createNewFile()
            }
            writer = new PrintWriter(file)
            writer.write(content)
            writer.write(lineSeparator)
            writer.flush()
            tableCount = tableCount + 1
          } catch {
            case e: NoPrimaryKeyException => {
              println(e)
            }
            case ex: ColumnTypeKeyException => {
              println(ex)
            }
          }

        }else{
          try {
            val content = new OracleTable2FlinkxJson(channel.toInt).readFile(item.getPath)
            val file = new File(s"${out}/${item.getName}.json")
            if (!file.exists()) {
              file.createNewFile()
            }
            writer = new PrintWriter(file)
            writer.write(content)
            writer.write(lineSeparator)
            writer.flush()
            tableCount = tableCount + 1
          } catch {
            case e: NoPrimaryKeyException => {
              println(e)
            }
            case ex: ColumnTypeKeyException => {
              println(ex)
            }
          }
        }

      })
    } else { //输入路径是文件，则直接处理该文件文件
      val content = new OracleTable2FlinkxJson(channel.toInt).readFile(in)
      writer.write(content)
    }


    writer.close()

    println(s"成功处理的表数量：${tableCount}")

    println("game is over!")
  }
}







