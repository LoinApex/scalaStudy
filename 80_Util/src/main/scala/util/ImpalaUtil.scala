package util

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ImpalaUtil {

  private val url: String = YamlUtil.getNested("impala.url")
  private val POOL_SIZE: Int = YamlUtil.getNested("impala.pool-size").toInt
  private val kudu_masters: String = YamlUtil.getNested("impala.kudu.master-addresses")

  private val totalConnection: AtomicInteger = new AtomicInteger(0)

  private val pool: mutable.Queue[Connection] = mutable.Queue()
  //Class.forName("org.apache.hive.jdbc.HiveDriver")
  //Class.forName("com.cloudera.impala.jdbc41.Driver")

  private def createConnection(): Connection = {
    val connection: Connection = DriverManager.getConnection(url)
    connection
  }

  private def enqueueConnect(connection: Connection): Unit = {
    val i: Int = totalConnection.get
    if (i <= POOL_SIZE && !connection.isClosed) {
      pool.enqueue(connection)
      totalConnection.incrementAndGet()
    } else {
      connection.close()
    }
  }

  def getConnection(): Connection = {
    if (pool.isEmpty && totalConnection.get <= POOL_SIZE) {
      val connection: Connection = createConnection
      enqueueConnect(connection)
      connection
    } else if (pool.nonEmpty) {
      totalConnection.decrementAndGet()
      pool.dequeue
    } else {
      throw new RuntimeException(s"not enough connection to get,the maxPoolSize is ${POOL_SIZE}")
    }
  }

  def queryList(sql: String, params: ListBuffer[Object], rs: ResultSet => Unit): Unit = {
    val connection: Connection = getConnection()
    val statement: PreparedStatement = connection.prepareStatement(sql)
    if (null != params && !params.isEmpty) {
      for ((param, index) <- params.zipWithIndex) {
        statement.setObject(index, param)
      }
      statement.addBatch()
    }
    val resultSet: ResultSet = statement.executeQuery()
    rs(resultSet)
    enqueueConnect(connection)
  }

  def executeBatch(sql: String, params: ListBuffer[Object]): Array[Int] = {
    val connection: Connection = getConnection()
    val statement: PreparedStatement = connection.prepareStatement(sql)
    if (null != params && !params.isEmpty) {
      for ((param, index) <- params.zipWithIndex) {
        statement.setObject(index + 1, param)
      }
      statement.addBatch()
    }
    val ints: Array[Int] = statement.executeBatch()
    enqueueConnect(connection)
    ints
  }

  def execute(sql: String): Unit = {
    val connection: Connection = getConnection()
    connection.prepareStatement(sql).execute()
    enqueueConnect(connection)
  }

  def createExternalTables(kuduTables: String*): Unit = {

    if (kuduTables.nonEmpty) {
      kuduTables.foreach(table => {
        execute(
          s"""
             |CREATE EXTERNAL TABLE IF NOT EXISTS ${table} STORED AS KUDU
             |TBLPROPERTIES(
             |  'kudu.table_name' = '${table}',
             |  'kudu.master_addresses' = '${kudu_masters}')
      """.stripMargin)
      })
    }

  }


}
