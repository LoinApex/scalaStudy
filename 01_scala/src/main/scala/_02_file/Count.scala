package _02_file



import java.io.File

import scala.collection.mutable.ListBuffer

object Count {
  /*  1.定义list
    2.选取文件
    3.读取行
    4
    */

  val list = List(
    "EP_SYS_MODIFY_DETAIL",
    "HS_LJFMX_D",
    "ZW_YSDFMX_D",
    "GK_TQYCJCYHXX",
    "ZW_SSDFMX_D",
    "FW_TDKHMX",
    "FW_YXYH",
    "SB_JLZCZTJL",
    "HS_DFTZRZ",
    "SB_DNBJBWCJL",
    "FW_XXFBDL",
    "FW_JLZDHJBQK",
    "FW_TDTZJL",
    "ZW_PJDYXX_D",
    "FW_DXFSMX",
    "HS_DFTZDMXXX",
    "ZW_DFHSLQFZJB",
    "FW_YKZZZLQD",
    "ZW_QFCSXX",
    "LC_CBXX_D",
    "HS_JLDXX_D",
    "ZW_YSDFJL_D",
    "FW_TDSJJBQK",
    "EP_SYS_MODIFY_LOG",
    "LC_JLDYDJ",
    "FW_XYDJPJGC",
    "HS_JLDDL_D",
    "FW_XYPJQZMX",
    "ZW_SSDFJL_D",
    "FW_KHSHXWMX",
    "WFRT_TASK_EXEC_INFO",
    "FW_YKDZHZLQD",
    "WFRT_TASK_SENDER",
    "LC_JLZDHCBSJ",
    "FW_YKTZFSBGXX",
    "ZW_YSJL",
    "ZW_YXTPJDYXX",
    "ZW_YDLW_DBSFMX",
    "FW_YKTZFSLSXX",
    "FW_YKLXRGLBGXX",
    "ZW_YDLW_DMXZ",
    "FW_YKLXRGLLSXX",
    "HS_DFFH_GDBLRZ",
    "ZW_SDYSJL",
    "GK_ZBTJJG_HIS",
    "ZW_YDLW_PLDKYC",
    "KH_TZFS",
    "SB_HGQWCJL",
    "KH_LXRGL"
  )


  def subdirs(dir: File): Iterator[File] = {
    val d = dir.listFiles.filter(_.isDirectory)
    val f = dir.listFiles.filter(_.isFile).toIterator
    f ++ d.toIterator.flatMap(subdirs _)
  }


  def main(args: Array[String]): Unit = {

    val reslist = new ListBuffer[String]
    val path: File = new File("C:\\Users\\FENG\\Documents\\WeChat Files\\hnliyaofeng\\FileStorage\\File\\2020-04\\all\\")

    var i = 0;
    for (d <- subdirs(path)) {
      for (li <- list) {
        if (d.getName.contains(li + ".sql")) {
          println(d + "  = " + li)
          reslist.append(d.getName)
        }
      }
    }

    val outPath: File = new File(path + "\\out\\")
    outPath.mkdir()

    println(reslist.size)
    for (res <- reslist) {
      //      println(res)
      //      println(path + "\\" + res)
      println(outPath + File.separator + res)
      //      FileUtils.copyFile(new File(path + "\\" + res), new File(outPath + "\\" + res))
    }
  }

}
