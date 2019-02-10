package containers

import scala.reflect.ClassTag

class ListContainerTest extends ContainerBehaviors[List] {
  override def createContainer[A: ClassTag](data: List[A]): List[A] = data

  override def toList[A: ClassTag](c: List[A]): List[A] = c

  it should behave like container
  
  it should "call foreach" in {
    var acc = 0
    createContainer(List(1, 2, 3)).foreach(i => acc += i)

    acc should be (6)
  }

  it should behave like pairContainer
}
