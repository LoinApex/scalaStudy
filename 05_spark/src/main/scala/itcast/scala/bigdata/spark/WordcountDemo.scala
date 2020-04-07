package itcast.scala.bigdata.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

class WordcountDemo {

}
/**
 * 要提交到集群运行，需要进行一下步骤操作：
 * 1、代码中应该在sparkconf中指定masterurl
 * 2、再将程序打成jar包
 * 3、将jar包上传到服务器
 * 4、用spark/bin下的spark-submit脚本进行提交
 * bin/spark-submit  --class cn.itcast.bigdata.spark.WordcountDemo \
 * --master spark://weekend01:7077  \
 * --deploy-mode cluster
 * <jar-path>
 * <main-method-params>
 * 
 */
object WordcountDemo {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf
    conf.setMaster("spark://weekend01:7077").setAppName("wordcount")

    val sc = new SparkContext(conf)
    
    val data = "hdfs://weekend01:9000/wordcount/srcdata/"
    val result = "hdfs://weekend01:9000/wordcount/output/"
    
    sc.textFile(data).flatMap { x => x.split(" ") }.map { x => (x,1) }.reduceByKey(_+_)
    .map(x=>(x._2,x._1)).sortByKey().map(x=>(x._2,x._1)).saveAsTextFile(result)
    
    sc.stop()
    
  }

}