package util

import snowflake.Sequence

/*
 * 描述信息  
 * User: Qing  
 * Date 2019/5/14
 * Version 1.0  
 */


object IdGenerator{

  private val sequeue=new Sequence(1,1)

  def nextId()=sequeue.nextId()
}
