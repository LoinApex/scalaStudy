package util

import java.sql.{Connection, DriverManager, ResultSet}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/*
 * 描述信息  
 *   
 * Date 2019/7/15
 * Version 1.0  
 */
object JdbcUtil extends Serializable {

  //连接池大小
  val connPoolSize = 10

  private val driver = Class.forName(YamlUtil.getNested("mysql.driver"))
  private val url = YamlUtil.getNested("mysql.url")
  private val user = YamlUtil.getNested("mysql.username")
  private val password = YamlUtil.getNested("mysql.password")

  // 数据库连接池
  private val connPool = mutable.Queue[Connection]()

  var i = 0
  while (i < connPoolSize) {
    val conn = DriverManager.getConnection(url, user, password)
    connPool += conn
    i = i + 1
  }

  def getConnection(): Connection = {
        while (connPool.size == 0) {
          Thread.sleep(10)
        }
        connPool.dequeue()
  }

  def connection(): Connection = {
    val conn = DriverManager.getConnection(url, user, password)
    conn
  }

  def closeConnection(conn: Connection): Unit = {
    conn.close()
  }

  /**
    * 执行增删改SQL语句
    *
    * @param sql
    * @param params
    * @return 影响的行数
    */
  def executeUpdate(sql: String, params: Array[Object]): Int = {
    var rtn = 0
    val conn = getConnection()
    conn.setAutoCommit(false)
    val pstmt = conn.prepareStatement(sql)
    if (params != null && params.length > 0) {
      for ((param, idx) <- params.zipWithIndex) {
        pstmt.setObject(idx + 1, param)
      }
    }
    rtn = pstmt.executeUpdate
    conn.commit()

    connPool += conn
    rtn

  }


  def executeQuery(sql: String, params: Array[Object], f: ResultSet => Unit): Unit = {
    var conn = getConnection()
    val pstmt = conn.prepareStatement(sql)
    if (params != null && params.length > 0) {
      for ((param, idx) <- params.zipWithIndex) {
        pstmt.setObject(idx + 1, param)
      }
    }
    val rs = pstmt.executeQuery()
    f(rs)
    connPool += conn
  }

  /**
    * 执行查询SQL语句
    *
    * @param sql
    * @param params
    * @param callback
    */
  def executeQuery(sql: String, params: Array[Object], callback: QueryCallback): Unit = {
    var conn = getConnection()
    conn.createStatement().execute("SET SESSION group_concat_max_len=65530;")
    val pstmt = conn.prepareStatement(sql)
    if (params != null && params.length > 0) {
      for ((param, idx) <- params.zipWithIndex) {
        pstmt.setObject(idx + 1, param)
      }
    }
    val rs = pstmt.executeQuery()
    callback.process(rs)
    connPool += conn
  }

  def executeQueryForList[T](sql: String, params: Array[Object], mapper: ResultSet => T): ArrayBuffer[T] = {
    val buf = ArrayBuffer[T]()
    this.executeQuery(sql, params, new QueryCallback {
      override def process(rs: ResultSet): Unit = {
        while (rs.next()) {
          val t = mapper(rs)
          buf += t
        }
      }
    })
    buf
  }


  /**
    * 批量执行SQL语句
    *
    * @param sql
    * @param paramsList
    * @return 每条SQL语句影响的行数
    */
  def executeBatch(sql: String, paramsList: ArrayBuffer[Any]): Array[Int] = {
    var conn = getConnection()

    conn.setAutoCommit(false)
    val pstmt = conn.prepareStatement(sql)
    if (paramsList != null && paramsList.size > 0) {
      for ((param, idx) <- paramsList.zipWithIndex) {
        pstmt.setObject(idx + 1, param)
      }
      pstmt.addBatch()
    }
    val rtn = pstmt.executeBatch()
    conn.commit()
    connPool += conn
    rtn
  }


  /**
    * 静态内部类：查询回调接口
    */
  trait QueryCallback {
    def process(rs: ResultSet)
  }

}
