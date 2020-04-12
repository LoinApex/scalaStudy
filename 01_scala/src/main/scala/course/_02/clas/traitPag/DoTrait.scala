package course._02.clas.traitPag

//抽象类
class DoTrait extends Trait {
  def todo = {
    println("hello lfdaslkf ")
  }
}

object DoTrait {
  def main(args: Array[String]): Unit = {
    val doTrait = new DoTrait
    doTrait.action
    doTrait.todo
  }
}