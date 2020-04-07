package util

import java.sql.Timestamp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.kudu.ColumnSchema.{ColumnSchemaBuilder, Encoding}
import org.apache.kudu.client.AsyncKuduScanner.ReadMode
import org.apache.kudu.client.KuduClient.KuduClientBuilder
import org.apache.kudu.client.KuduScanner.KuduScannerBuilder
import org.apache.kudu.client.SessionConfiguration.FlushMode
import org.apache.kudu.client._
import org.apache.kudu.{ColumnSchema, Schema, Type}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object KuduUtil {

  private val kuduMasters = YamlUtil.getNested("kudu.masters")
  private val timeOut: Int = YamlUtil.getNested("kudu.operation-timeout").toInt
  private val client: KuduClient = new KuduClientBuilder(kuduMasters)
    .defaultAdminOperationTimeoutMs(timeOut)
    .defaultOperationTimeoutMs(timeOut)
    .defaultSocketReadTimeoutMs(timeOut)
    .build()

  def getKuduClient(): KuduClient = {
    client
  }

  def getKuduTable(tableName: String): KuduTable = {
    val client = getKuduClient
    if (!client.tableExists(tableName)) return null
    client.openTable(tableName)
  }

  def tableExists(tableName: String): Boolean = {
    getKuduClient.tableExists(tableName)
  }

  def batchInsert(table: String, data: List[Map[String, Any]]): RowErrorsAndOverflowStatus = {
    batchOperate(KuduOperation.INSERT, table, data)
  }

  def batchUpsert(table: String, data: List[Map[String, Any]]): RowErrorsAndOverflowStatus = {
    batchOperate(KuduOperation.UPSERT, table, data)
  }

  def batchUpdate(table: String, data: List[Map[String, Any]]): RowErrorsAndOverflowStatus = {
    batchOperate(KuduOperation.UPDATE, table, data)
  }

  def batchDelete(table: String, data: List[Map[String, Any]]): RowErrorsAndOverflowStatus = {
    batchOperate(KuduOperation.DELETE, table, data)
  }


  def batchOperate(operation: KuduOperation.Value, table: String, data: List[Map[String, Any]]): RowErrorsAndOverflowStatus = {
    val session: KuduSession = getKuduClient.newSession()
    session.setFlushMode(FlushMode.MANUAL_FLUSH)
    //val maxBufferSize = 2000 //最大
    //session.setMutationBufferSpace(maxBufferSize)

    var error: RowErrorsAndOverflowStatus = null
    data.foreach(map => {
      operate(operation, table, map, false)
    })
    session.flush()
    if (session.countPendingErrors >= 1) {
      error = session.getPendingErrors
    }
    error
  }


  def operate(operation: KuduOperation.Value, table: String, map: Map[String, Any], flush: Boolean = true): OperationResponse = {
    if (null == map || map.isEmpty) return null
    val kuduTable: KuduTable = getKuduTable(table)

    val session: KuduSession = getKuduClient.newSession()
    if (flush)
      session.setFlushMode(SessionConfiguration.FlushMode.AUTO_FLUSH_SYNC)

    val opt = operation match {
      case KuduOperation.UPSERT => kuduTable.newUpsert()
      case KuduOperation.INSERT => kuduTable.newInsert()
      case KuduOperation.DELETE => kuduTable.newDelete()
      case KuduOperation.UPDATE => kuduTable.newUpdate()
    }
    opt.setExternalConsistencyMode(ExternalConsistencyMode.CLIENT_PROPAGATED)
    val row: PartialRow = opt.getRow
    map.foreach(v => {
      val key = v._1.toLowerCase()
      val value = v._2
      value match {
        case p: Double => row.addDouble(key, value.asInstanceOf[Double])
        case p: Array[Byte] => row.addBinary(key, value.asInstanceOf[Array[Byte]])
        case p: Boolean => row.addBoolean(key, value.asInstanceOf[Boolean])
        case p: Byte => row.addByte(key, value.asInstanceOf[Byte])
        case p: Float => row.addFloat(key, value.asInstanceOf[Float])
        case p: Int => row.addInt(key, value.asInstanceOf[Int])
        case p: Long => row.addLong(key, value.asInstanceOf[Long])
        case p: Short => row.addShort(key, value.asInstanceOf[Short])
        case p: String => row.addString(key, value.asInstanceOf[String])
        case _ => row.addStringUtf8(key, value.asInstanceOf[Array[Byte]])
      }
    })
    session.apply(opt)
  }


  /**
    * this method pls care that will take about 115107 milliseconds on table which own  1000000 records
    *
    * @param table
    * @return
    */
  def countTable(table: String): Long = {
    this.synchronized {
      val client = getKuduClient
      val kuduTable: KuduTable = getKuduTable(table)
      val scannerBuilder: KuduScannerBuilder = client.newScannerBuilder(kuduTable)
      scannerBuilder.readMode(ReadMode.READ_LATEST)
        .cacheBlocks(true)
        .scanRequestTimeout(client.getDefaultOperationTimeoutMs)
      val scanner: KuduScanner = scannerBuilder.build()
      var count: Long = 0
      while (scanner.hasMoreRows) {
        count += scanner.nextRows().getNumRows
      }
      count
    }
  }

  def scanTable(table: String, limit: Long): List[Map[String, Any]] = {
    val kuduTable: KuduTable = getKuduTable(table)
    val client = getKuduClient
    val scannerBuilder: KuduScannerBuilder = client.newScannerBuilder(kuduTable)
    scannerBuilder.readMode(ReadMode.READ_LATEST)
      .limit(limit)
      .cacheBlocks(true)
      .scanRequestTimeout(client.getDefaultOperationTimeoutMs)
    val scanner: KuduScanner = scannerBuilder.build()
    converRowResults(scanner)
  }


  def scanByCondition(table: String, conditions: List[(String, KuduPredicate.ComparisonOp, Any)], limit: Int = -1): List[Map[String, Any]] = {
    val kuduTable: KuduTable = getKuduTable(table)
    if (null == kuduTable) {
      return List[Map[String, Any]]()
    }
    val client = getKuduClient
    val scannerBuilder: KuduScannerBuilder = client.newScannerBuilder(kuduTable).readMode(ReadMode.READ_LATEST)
    for ((colName, op, value) <- conditions) {
      if (null == value) {
        scannerBuilder.addPredicate(KuduPredicate.newIsNullPredicate(kuduTable.getSchema.getColumn(colName.toLowerCase())))
      } else {
        //scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(convert(condition._1), condition._2, condition._3.asInstanceOf[String]))
        val col = kuduTable.getSchema.getColumn(colName.toLowerCase())
        value match {
          case p: Array[Byte] => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Array[Byte]]))
          case p: Double => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Double]))
          case p: Float => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Float]))
          case p: Long => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Long]))
          case p: Int => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Int]))
          case p: String => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[String]))
          case p: Boolean => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Boolean]))
          case p: Timestamp => scannerBuilder.addPredicate(KuduPredicate.newComparisonPredicate(col, op, value.asInstanceOf[Timestamp].getTime))
        }
      }
    }
    if (-1 != limit) {
      scannerBuilder.limit(limit)
    }
    val scanner: KuduScanner = scannerBuilder.build()
    converRowResults(scanner)
  }


  private def converRowResults(scanner: KuduScanner): List[Map[String, Any]] = {
    var listBuffer: ListBuffer[Map[String, Any]] = ListBuffer()
    while (scanner.hasMoreRows) {
      val row: RowResultIterator = scanner.nextRows()
      while (row.hasNext) {
        var rowData: Map[String, Any] = Map()
        val rowResult: RowResult = row.next()
        val schema: Schema = rowResult.getSchema
        val columns: mutable.Buffer[ColumnSchema] = schema.getColumns
        columns.foreach(cs => {
          cs.getType match {
            case Type.BINARY => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getBinary(cs.getName)))
            case Type.BOOL => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getBoolean(cs.getName)))
            case Type.DOUBLE => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getDouble(cs.getName)))
            case Type.FLOAT => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getFloat(cs.getName)))
            case Type.STRING => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getString(cs.getName)))
            case Type.INT8 => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getByte(cs.getName)))
            case Type.INT16 => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getShort(cs.getName)))
            case Type.INT32 => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getInt(cs.getName)))
            case _ => rowData += (cs.getName -> (if (rowResult.isNull(cs.getName)) null else rowResult.getLong(cs.getName)))
          }
        })
        if (!rowData.isEmpty) listBuffer += rowData
      }
    }
    listBuffer.toList
  }

  def dropTable(table: String): Unit = {
    this.synchronized {
      val client = getKuduClient
      if (tableExists(table)) {
        client.deleteTable(table)
      }
    }
  }

  def createTable(table: String, columns: List[KuduColumnInfo], replicas: Int = 3): Unit = {
    //if table exits, need update table structure or return directly ?
    val client = getKuduClient
    if (client.tableExists(table)) return

    var keys: ListBuffer[String] = ListBuffer()
    var columnSchemas: ListBuffer[ColumnSchema] = ListBuffer()
    columns.foreach(kuduColumn => {
      if (kuduColumn.isKey) keys += kuduColumn.columnName
      columnSchemas += convert(kuduColumn)
    })
    val schema = new Schema(columnSchemas)

    val createTableOptions = new CreateTableOptions
    createTableOptions.addHashPartitions(keys, replicas * 2)
    createTableOptions.setNumReplicas(replicas)
    client.createTable(table, schema, createTableOptions)
  }

  private def convert(columnInfo: KuduColumnInfo): ColumnSchema = {
    val builder: ColumnSchemaBuilder = new ColumnSchemaBuilder(columnInfo.columnName, columnInfo.columnType)
    builder.compressionAlgorithm(ColumnSchema.CompressionAlgorithm.LZ4)
      .defaultValue(if (columnInfo.nullable) null else columnInfo.columnType match {
        case Type.BINARY => Array[Byte](0)
        case Type.INT8 => 0.toByte
        case Type.INT16 => 0.toShort
        case Type.BOOL => false
        case Type.DOUBLE => 0.0
        case Type.FLOAT => 0.0
        case Type.STRING => ""
        case Type.UNIXTIME_MICROS => System.currentTimeMillis()
        case _ => 0
      })
      .desiredBlockSize(4096)
      .key(columnInfo.isKey)
      .nullable(columnInfo.nullable)
      .encoding(columnInfo.columnType match {
        case Type.BOOL => Encoding.RLE
        case Type.STRING => Encoding.DICT_ENCODING
        case Type.BINARY => Encoding.DICT_ENCODING
        case _ => Encoding.BIT_SHUFFLE
      }).build()
  }

  /**
    * add columns
    * must be none primary columns
    *
    * @param table
    * @param columns
    */
  def alterTable(table: String, columns: List[KuduColumnInfo]): Unit = {
    this.synchronized {
      val client = getKuduClient
      if (!client.tableExists(table)) return
      val alterTableOptions: AlterTableOptions = new AlterTableOptions
      columns.foreach(kuduColumn => {
        alterTableOptions.addColumn(convert(kuduColumn))
      })
      client.alterTable(table, alterTableOptions)
      while (!client.isAlterTableDone(table)) {
      }
    }
  }

  def addColumnsIfNotExists(table: String, columns: List[KuduColumnInfo]): Unit = {
    this.synchronized {
      val client = getKuduClient
      if (!client.tableExists(table)) return
      val kuduTable: KuduTable = getKuduTable(table)
      val tableSchema: Schema = kuduTable.getSchema
      val schemas: Seq[ColumnSchema] = tableSchema.getColumns
      val temp: ListBuffer[KuduColumnInfo] = ListBuffer()
      val iterator = columns.iterator
      while (iterator.hasNext) {
        val columnInfo: KuduColumnInfo = iterator.next()
        var contains = false
        schemas.foreach(col => {
          if (col.getName.equalsIgnoreCase(columnInfo.columnName) || temp.contains(columnInfo)) {
            contains = true
          }
        })
        if (!contains) temp += columnInfo
      }
      alterTable(table, temp.toList)
    }
  }

  def getKuduMeta(tables: mutable.Buffer[String]): Map[String, Schema] = {
    var tableList: mutable.Buffer[String] = tables
    val client = getKuduClient
    if (tableList.isEmpty) {
      val tablesList: ListTablesResponse = client.getTablesList
      tableList = tablesList.getTablesList
    }
    var metas: Map[String, Schema] = Map()
    tableList.foreach(table => {
      if (tableExists(table)) {
        metas += table -> getKuduTable(table).getSchema
      } else {
        metas += table -> null
      }
    })
    metas
  }

  def close(): Unit = {
//    val client = getKuduClient
//    client.close()
  }

  case class KuduColumnInfo(
                             val columnName: String,
                             val columnType: Type,
                             val isKey: Boolean,
                             val nullable: Boolean
                           ) {
    override def toString: String = JSON.toJSONString(this, SerializerFeature.SkipTransientField)

  }

  object KuduOperation extends Enumeration {
    //type KuduOperation = Value
    val INSERT = Value("insert")
    val UPDATE = Value("update")
    val UPSERT = Value("upsert")
    val DELETE = Value("delete")
  }

}

