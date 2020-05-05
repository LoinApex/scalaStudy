package script

import java.io.File
import java.util.Properties

import org.apache.commons.io.FileUtils

/*
 * 描述信息  
 *   
 * Date 2020/3/18
 * Version 1.0  
 */
object RemoveTable {

  def main(args: Array[String]): Unit = {

    val properties=new Properties()
    val path=this.getClass.getResourceAsStream("/scripts/remove-table.properties")
    properties.load(path)

    val inPath="D:\\ccc\\all"
    val file=new File(inPath)
    if(file.isDirectory){
      file.listFiles().foreach(item=>{
        var itemName=item.getName
        var newName:String=null
        val suffix= itemName.substring(itemName.lastIndexOf("_"),itemName.length)
        if(suffix.startsWith("_2020")){
          newName=itemName.substring(0,itemName.lastIndexOf("_"))
        }else {
          newName = itemName.substring(0, itemName.lastIndexOf("."))
        }

        if(properties.containsKey(newName)){
          item.delete()
        }

      })
    }




  }
}
