package _02_file

import java.io.File

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
}
