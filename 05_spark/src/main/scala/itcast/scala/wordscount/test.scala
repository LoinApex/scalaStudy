package itcast.scala.wordscount

object test {

  def sayHello(name: String = "daiv"): Unit = {
    print("hello     " + name)
  }

  def add(x: Int*) = {
    for (i <- x) {
      println(i)

    }
    x.sum

  }

  def main(args: Array[String]): Unit = {
    //    sayHello("")

    val bb = add(2, 3, 56, 6)
    println(bb)

  }

}