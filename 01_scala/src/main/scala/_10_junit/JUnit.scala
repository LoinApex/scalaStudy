package _10_junit

import org.junit.Assert._
import org.junit._

// junit 4使用
// http://icejoywoo.github.io/2018/09/28/scala-unit-test.html
object JUnit {
  @BeforeClass
  def beforeClass(): Unit = {
    println("before class in obj")
  }

  @AfterClass
  def afterClass(): Unit = {
    println("after class in obj")
  }
}

//JUnit4 还提供了 BeforeClass 和 AfterClass 在整个测试的前后执行一次，要求方法必须是 static 的。
// 在 Java 中通过 static 关键字来定义，
// 在 Scala 中需要实用伴生对象，定义一个 object 来实现 static 方法。

class JUnit() {
  val str="123456"


  @Before
  def before(): Unit = {
    println("before test")
  }

  @After
  def after(): Unit = {
    println("after test")
  }

  @Test
  def testList(): Unit = {
    println("testList")
    println(str)
    val list = List("java", "b")

    assertEquals(List("java", "b"), list)
    assertNotEquals(List("b", "java"), list)
  }


}
