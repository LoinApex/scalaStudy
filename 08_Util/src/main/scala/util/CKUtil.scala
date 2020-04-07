package util

import java.sql.{PreparedStatement, ResultSet}

import ru.yandex.clickhouse.{BalancedClickhouseDataSource, ClickHouseConnection}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object CKUtil {

  val url: String = YamlUtil.getNested("clickhouse.url")
  val cache_size = 10
  val connectQueue: mutable.Queue[ClickHouseConnection] = mutable.Queue[ClickHouseConnection]()
  init

  private def init: Unit = {
    for (i <- 1 to cache_size) {
      connectQueue.enqueue(createConnection)
    }
  }

      def query[T](sql: String, rs: ResultSet => T, params: Array[Any] = null): ListBuffer[T] = {
        val connection: ClickHouseConnection = getConnection
        val statement: PreparedStatement = getStatement(sql, connection, params)
        val data: ListBuffer[T] = ListBuffer[T]()
        val resultSet: ResultSet = statement.executeQuery()
        while (resultSet.next()) {
      data += rs(resultSet)
    }
    enqueue(connection)
    data
  }

  def execute(sql: String, params: Array[Any] = null): Unit = {
    val connection: ClickHouseConnection = getConnection
    //connection.setAutoCommit(false)
    val statement: PreparedStatement = getStatement(sql, connection, params)
    statement.executeUpdate()
    enqueue(connection)
  }

  def getConnection: ClickHouseConnection = {
    if (connectQueue.isEmpty) {
      enqueue(createConnection)
    }
    val connection: ClickHouseConnection = connectQueue.dequeue()
    if (connection.isClosed) getConnection
    connection
  }

  private def enqueue(connection: ClickHouseConnection): Unit = {
    if (null != connection && !connection.isClosed) {
      connectQueue.enqueue(connection)
    }
  }

  private def createConnection: ClickHouseConnection = {
    new BalancedClickhouseDataSource(url).getConnection
  }

  private def getStatement(sql: String, connection: ClickHouseConnection, params: Array[Any] = null): PreparedStatement = {
    val statement: PreparedStatement = connection.prepareStatement(sql)
    if (null != params && !params.isEmpty) {
      var i = 1
      params.foreach(param => {
        statement.setObject(i, param)
        i += 1
      })
    }
    statement
  }
}
