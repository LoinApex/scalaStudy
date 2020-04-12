package _02_file

import java.io.{File, PrintWriter}

import org.junit.Test

import scala.io.Source


class FileOper {
  //============================文件操作======================================

  val file = new File("G:\\南网项目\\任务\\20200330_结构化数据实时入库\\all\\PMS_GL_AABC.sql")
  val fileP = "G:\\南网项目\\任务\\20200330_结构化数据实时入库\\all"
  val filePath = new File("G:\\南网项目\\任务\\20200330_结构化数据实时入库\\all")

  /**
   * 创建文件夹
   */
  def creDelDir: Unit = {
    file.mkdir()

    filePath.createNewFile()
    filePath.deleteOnExit()
    filePath.list()
  }

  /**
   * 遍历文件夹
   */
  def listFile(p: String): Unit = {
    val path: File = new File(p)
    //集合操作方式
    for (file <- path.listFiles())
      println(file)
  }

  /**
   * 递归遍历本文件夹以及子文件夹的文件
   *
   * @param dir
   * @return
   */
  def subdirs(dir: File): Iterator[File] = {
    val d = dir.listFiles.filter(_.isDirectory)
    val f = dir.listFiles.filter(_.isFile).toIterator
    f ++ d.toIterator.flatMap(subdirs _)

    //    FileUtils.listFilesAndDirs(file)
  }

  /**
   * 拷贝文件
   */
  def copyFile2File: Unit = {
    //    FileUtils.copyFile(new File(""), new File(""))
  }

  /**
   * 拷贝文件到文件夹
   */
  def copeFile2Dir: Unit = {
    //    FileUtils.copyFileToDirectory(file, new File(fileP + File.separator + "aa"))
  }


  @Test
  def aa: Unit = {
    println("fdsfs")
  }


  //===============================文件内容操作===================================
  /**
   * 文本文件按行读取
   */
  @Test
  def readLine(): Unit = {
    // Source.fromFile的第一个参数可以是字符串或java.io.File
    //第二个是字符编码参数
    for (line <- Source.fromFile(file, "UTF-8").getLines) {
      println(line);
    }
    println("============")
    //file.getLines()返回的是一个Iterator
    val lines = Source.fromFile(file).getLines.toList;
    for (line <- lines) {
      println(line.reverse);
    }

    println("============")
    //    val strings = FileUtils.readLines(file) //读取所有行
    //    println(strings)


    //==================================================================


  }

  /**
   *
   * 读写文件
   */
  @Test
  def ReadWriteFile: Unit = {

    val file = "D:\\ProWork\\aa\\a.txt";
    println("写文件")
    val out = new PrintWriter(file)
    for (i <- 1 to 10) out.println(i)
    // use string format
    val quantity = 100
    val price = .1
    out.print("%6d %10.2f".format(quantity, price)) //d整型 f浮点数 占6位
    out.close()

    println("读文件")
    //读取中文的就必须先把n.txt格式为utf-8格式，然后读取时增加("UTF-8")
    val lines = Source.fromFile(file)("UTF-8").getLines()
    lines.foreach(println)

  }

}
