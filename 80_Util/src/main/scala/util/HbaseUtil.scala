package util

import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.{Admin, Connection, ConnectionFactory}

object HbaseUtil  extends Serializable {

  val zkQuorum = YamlUtil.getNested("hbase.zookeeper-quorum")
  val zkPort = YamlUtil.getNested("hbase.zookeeper-port")
  //val clientTimeout = YamlUtil.getNested("hbase.client.operation-timeout").toInt
  //val clientTimeoutPeriod = YamlUtil.getNested("hbase.client.scanner-timeout-period").toInt

  val conf = HBaseConfiguration.create()
  conf.addResource("/core-site.xml")
  conf.addResource("/hdfs-site.xml")
  conf.addResource("/hbase-site.xml")

  conf.set(HConstants.ZOOKEEPER_QUORUM, zkQuorum)
  conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, zkPort)
  //conf.setInt(HConstants.HBASE_CLIENT_OPERATION_TIMEOUT, clientTimeout)
  //conf.setInt(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, clientTimeoutPeriod)

  private val connection = ConnectionFactory.createConnection(conf)


  def getConnection():Connection={
    if(connection.isClosed)
      ConnectionFactory.createConnection(conf)
    else connection
  }

  def createTable(database:String,tableName:String):Unit={
    this.synchronized{
      val admin: Admin = getConnection.getAdmin
      val nameSpace: NamespaceDescriptor = NamespaceDescriptor.create(database).build()
      val nameSpaces: Array[String] = getNamespaces()
      if(!nameSpaces.contains(database)){
        admin.createNamespace(nameSpace)
      }
      val table: TableName = TableName.valueOf(database,tableName)
      if(!admin.tableExists(table)){
        val descriptor = new HTableDescriptor(table)
        val hColumnDescriptor = new HColumnDescriptor("CF_DL")
        descriptor.setConfiguration("hbase.table.sanity.checks","false")
        descriptor.addFamily(hColumnDescriptor)
        admin.createTable(descriptor)
      }
    }
  }

  def getNamespaces():Array[String]={
    val admin: Admin = getConnection.getAdmin
    admin.listNamespaceDescriptors().map(_.getName)
  }

  def close():Unit={
    if(connection!=null)
      connection.close()
  }
}
