package course._02.clas

object T5 {
  def main(args: Array[String]) {

    /*类(class)
	 类是对象的模板，通过构造类，能够使用new关键字声明一系列同结构的对象
	 对象(object)
	 除了使用类构造对象模板，可以使用object构造单例对象
	 继承
	 继承是类的拓展
	 特质
	 一个类只能继承自一个父类，但可以由多个特质拓展而成*/
    val one = new C5
    val a = one.value2

    one.add()
    val mm = one.plus('G')
    println(mm)

  }

}
