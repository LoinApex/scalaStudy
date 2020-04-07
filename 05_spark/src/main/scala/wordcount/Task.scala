package wordcount

class Task { }

 //创建对象，有两个参数，第一个参数是控制台输入数据字符串，第二个参数就是一个函数算法
case class MapperTask(line:String, mfun:(String) => (List[(String,Int)]) )
  
//创建对象，有两个参数，第一个参数是map(key就是控制台中的单词,value每个单词记录1次)，将这些单词的总共出现的次数进行累加返回
case class ReduceTask(kv:(String,Int), rfun:(Int,Int) => Int)