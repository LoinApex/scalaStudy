package itcast.scala.demosql

import java.sql.DriverManager

class TestSparkSqlThrift {

}
/**
 * 使用hive的jdbc驱动访问sparksql的thriftserver或者hive的thriftserver
 */
object TestSparkSqlThrift {

  def main(args: Array[String]): Unit = {

    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val conn = DriverManager.getConnection("jdbc:hive2://weekend01:10000", "hadoop", "");

    try {
      val state = conn.createStatement()
      val res = state.executeQuery("select * from tbl_stockdetail where itemamout>3000")
      while (res.next()) {

        val orderid = res.getString("orderid")
        val itmenum = res.getString("itmenum")
        val itemid = res.getString("itemid")
        val itemprice = res.getString("itemprice")
        val itemamout = res.getString("itemamout")

        println(orderid + " " + itmenum + " " + itemid + " " + itemprice + " " + itemamout)

      }
    } catch {
      case e: Exception => e.printStackTrace()

    }
    conn.close()
  }

}