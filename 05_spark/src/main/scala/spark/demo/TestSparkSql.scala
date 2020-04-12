package spark.demo

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext
/**
 * 测试hive数据跟其他数据的混合查询
 */
//cn.itcast.bigdata.sparksql.TestSparkSql
class TestSparkSql {

}

case class PepInfo(name:String,addr:String,phone:String)


object TestSparkSql {
  
  def main(args: Array[String]): Unit = {
    
    val conf = new SparkConf()
    //如果任务提交后报错classnotfound，可以在这里setjars来指定job到jar包
    conf.setMaster("spark://weekend01:7077").setAppName("eclipsesql").setJars(Array("/home/hadoop/scalaworkspace/sparksql/target/sparksql-0.0.1-SNAPSHOT.jar"))
    
    val sc = new SparkContext(conf)
    
    val hiveContext = new HiveContext(sc)
    
    import hiveContext.implicits._
    
    
//    val resDF = hiveContext.sql("select name,count(name),age from people_hive_tbl group by name,age")
//    resDF.save("hdfs://weekend01:9000/sparksql/out1","json")
    
    val pepinfoDF = sc.textFile("/sparksql/data/peopleinfo.data").map { x => x.split(",") }.map { x => PepInfo(x(0),x(1),x(2)) }.toDF
    pepinfoDF.registerTempTable("pepinfotbl")
    
    val resDF2 = hiveContext.sql("select a.name,a.age,b.addr,b.phone from people_hive_tbl a join pepinfotbl b on a.name=b.name")
    resDF2.save("hdfs://weekend01:9000/sparksql/outjoin/","json")
    
    sc.stop()
    
  }
  
  
}
