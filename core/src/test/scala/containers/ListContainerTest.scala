package containers

import scala.language.higherKinds
import scala.reflect.ClassTag
import Container.ops._

class ListContainerTest extends ContainerBehaviors[List] {
  override def createContainer[A: ClassTag](data: List[A]): List[A] = data

  override def toList[A: ClassTag](c: List[A]): List[A] = c

  it should behave like container

  it should "call foreach" in {
    def foreachIt[C[_]: Container](c: C[Int], f: Int => Unit): Unit = c.foreach(f)

    var acc = 0
    foreachIt(createContainer(List(1, 2, 3)), i => acc += i)

    acc should be (6)
  }

  it should behave like pairContainer
}
