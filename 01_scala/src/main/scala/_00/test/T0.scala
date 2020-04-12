package _00.test

import org.junit.Test

class T0 {
  @Test
  def t1: Unit = {
    println(
      s"""
         |usage like:
         |    scala -cp log-stream-0.0.1-SNAPSHOT.jar cn.csg.deltalake.importdata.ImportDataToMysql excelPath [sheetName]
         |notes : excel file's sheetName must be same as table name. otherwise, will import failed.
         |        if give sheetName parameter, just only import sheetName data to mysql
         | maybe you can use below command to backup current table data:
         | mysqldump --host= --port= --user= --password=  --result-file=output_savefile database table
        """.stripMargin)
//    使用多行字符串 | 进行分隔
  }
}
