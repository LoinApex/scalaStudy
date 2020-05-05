package itcast.scala.sparkStreaming.course

import java.io.PrintWriter
import java.net.ServerSocket
import scala.collection.mutable.ArrayBuffer
import java.util.Random


class DataSend {

}

/**
 * @author feng
 *  模拟去一个端口发送数据
 *  1.准备数据内容
 *  2.产生随机数据
 *  3.创建socket监听器,等到客户端连接
 *  4.往客户端发送数据
 *
 *
 */

object DataSend {

  //  准备数据内容池,返回指定内容
  def genderateData(index: Int) {
    //    准备数据内容
    val ab = ArrayBuffer[Char]()
    for (i <- 65 to 90) {
      ab += i.toChar

    }
    //      返回随机数据
    val data = ab(index)
    print(data)
    data

  }
  //  创建网络监听器,连接客户端,发送数据 
  def main(args: Array[String]): Unit = {
    val listener = new ServerSocket(8880)
    while (true) {
      val socket = listener.accept()
      new Thread {

        override def run() {
          val pwout = new PrintWriter(socket.getOutputStream)

          while (true) {

            Thread.sleep(200)

            val redata = genderateData((new Random(20)).nextInt())
//            pwout.write(redata)
            print(redata)

          }

        }

      }

    }
  }

}


