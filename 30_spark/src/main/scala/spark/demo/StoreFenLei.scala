package spark.demo

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.Row
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.clustering.KMeans

class StoreFenLei {

}
//cn.itcast.bigdata.excersize.StoreFenLei
object StoreFenLei {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf
    conf.setMaster("spark://weekend01:7077").setAppName("mlib")
    val sc = new SparkContext(conf)

    val hiveContext = new HiveContext(sc)
    hiveContext.sql("use saledata")
    hiveContext.sql("set  spark.sql.shuffle.partitions=20")
    val sqldata = hiveContext.sql("select a.locationid, sum(b.qty) totalqty,sum(b.amount) totalamount from tblStock a join tblstockdetail b on a.ordernumber=b.ordernumber group by a.locationid")
    val parsedData = sqldata.map {
      case Row(_, totalqty, totalamount) =>
        val features = Array[Double](totalqty.toString().toDouble, totalamount.toString().toDouble)
        Vectors.dense(features)

    }

    val numClusters = 3
    val numIterations = 20

    val model = KMeans.train(parsedData, numClusters, numIterations)

    val result2 = sqldata.map {

      case Row(locationid, totalqty, totalamount) =>
        val features = Array[Double](totalqty.toString().toDouble, totalamount.toString().toDouble)
        val lineVector = Vectors.dense(features)
        val prediction = model.predict(lineVector)
        locationid + " " + totalqty + " " + totalamount + " " + prediction 

    }
    result2.saveAsTextFile("hdfs://weekend01:9000/sparksql/kmeans/")

    sc.stop
  }
  

}