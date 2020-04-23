package course._06.wc.cons

class Task {
}

//  创建对象 控制台输入字符串 函数
case class MapperTask(line: String, nfun: (String) => List[(String, Int)])

//  创建对象, 第一个参数: map(key控制台单词,value每个单词记录一次), 将这些单词总共出现的次数累加返回
case class ReducerTask(kv: (String, Int), rfun: (Int, Int) => Int)


