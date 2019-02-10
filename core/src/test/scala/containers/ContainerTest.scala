package containers

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.OptionValues._
import Container.ops._

import scala.language.higherKinds
import scala.reflect.ClassTag


abstract class ContainerBehaviors[C[_]: Container: PairContainer](implicit ev: ClassTag[C[_]])
  extends FlatSpec with Matchers {
  def createContainer[A: ClassTag](data: List[A]): C[A]

  def toList[A: ClassTag](c: C[A]): List[A]

  def container: Unit = {
    behavior of s"${implicitly[reflect.ClassTag[C[_]]].runtimeClass.getSimpleName} Container"

    it should "call map" in {
      toList(createContainer(List(1, 2, 3, 4))
        .map(_.toString)) should contain theSameElementsInOrderAs List("1", "2", "3", "4")
    }

    it should "call flatMap" in {
      toList(createContainer(List((1, 2), (3, 4)))
        .flatMap(t => Seq(t._1, t._2))) should contain theSameElementsInOrderAs Seq(1, 2, 3, 4)
    }

    it should "do nothing when calling repartition" in {
      toList(createContainer(List(1, 2, 3, 4))
        .repartition(1000)) should contain theSameElementsAs List(1, 2, 3, 4)
    }

    it should "count all elements without filter" in {
      createContainer(List(1, 2, 3, 4)).count(_ => true) should be(4)
    }

    it should "count elements fulfilling filter" in {
      createContainer(List(1, 2, 3, 4)).count(_ % 2 == 0) should be(2)
    }

    it should "call reduce" in {
      createContainer(List(1, 2, 3, 4)).reduce(_ + _) should be(10)
    }

    it should "call distinct" in {
      toList(createContainer(List(1, 1, 2, 3, 2, 3, 1)).distinct) should contain theSameElementsAs List(1, 2, 3)
    }

    it should "call sortBy" in {
      toList(createContainer(List(3, 5, 1, 8, 4, 6, 2, 9 ,7)))
        .sortBy(i => i) should contain theSameElementsInOrderAs List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    }

    it should "call sortBy with reverse ordering" in {
      toList(createContainer(List(3, 5, 1, 8, 4, 6, 2, 9 ,7))
        .sortBy(i => i, false)) should contain theSameElementsInOrderAs List(9, 8, 7, 6, 5, 4, 3, 2, 1)
    }

    it should "call intersect" in {
      val l1 = createContainer(List(1, 2, 3, 4, 5))
      val l2 = createContainer(List(3, 4, 5, 6, 7))

      toList(l1.intersection(l2)) should contain theSameElementsAs List(3, 4, 5)
    }

    it should "calculate catesian product of 2 lists" in {
      val l1 = createContainer(List(1, 2, 3))
      val l2 = createContainer(List("a", "b", "c"))

      toList(l1.cartesian(l2)) should contain theSameElementsAs List(
        (1, "a"),
        (1, "b"),
        (1, "c"),
        (2, "a"),
        (2, "b"),
        (2, "c"),
        (3, "a"),
        (3, "b"),
        (3, "c")
      )
    }

    it should "call groupBy" in {
      val grouped = toList(createContainer(List(1, 2, 3, 4, 5, 6)).groupBy(i => i % 2))

      grouped.find(_._1 == 0).value._2 should contain theSameElementsAs List(2, 4, 6)
      grouped.find(_._1 == 1).value._2 should contain theSameElementsAs List(1, 3, 5)
    }

    it should "call zip" in {
      val l1 = createContainer(List(1, 2, 3))
      val l2 = createContainer(List("a", "b", "c"))

      toList(l1.zip(l2)) should contain theSameElementsAs List((1, "a"), (2, "b"), (3, "c"))
    }
  }

  def pairContainer: Unit = {
    behavior of s"${implicitly[reflect.ClassTag[C[_]]].runtimeClass.getSimpleName} PairContainer"

    it should "reduce by key properly" in {
      toList(createContainer(List(("a", 1), ("b", 1), ("a", 1), ("c", 1)))
        .reduceByKey(_ + _)) should contain theSameElementsAs List(("a", 2), ("b", 1), ("c", 1))
    }

    it should "combineByKey properly" in {

      toList(createContainer(List(("a", 1), ("b", 1), ("a", 1), ("c", 1)))
        .combineByKey[String](_.toString, (acc, i) => acc + i, _ + _)) should contain theSameElementsAs List(
        ("a", "11"),
        ("b", "1"),
        ("c", "1")
      )
    }
  }
}
//abstract class ContainerTest[C[_]: Container: PairContainer](implicit ev: ClassTag[C[_]]) extends FlatSpec with Matchers {
//
//  def createContainer[A: ClassTag](data: List[A]): C[A]
//
//  def toList[A: ClassTag](c: C[A]): List[A]
//
//  behavior of s"${implicitly[reflect.ClassTag[C[_]]].runtimeClass.getSimpleName} Container"
//
//  it should "call map" in {
//    toList(createContainer(List(1, 2, 3, 4))
//      .map(_.toString)) should contain theSameElementsInOrderAs List("1", "2", "3", "4")
//  }
//
//  it should "call flatMap" in {
//    toList(createContainer(List((1, 2), (3, 4)))
//      .flatMap(t => Seq(t._1, t._2))) should contain theSameElementsInOrderAs Seq(1, 2, 3, 4)
//  }
//
//  it should "do nothing when calling repartition" in {
//    toList(createContainer(List(1, 2, 3, 4))
//      .repartition(1000)) should contain theSameElementsAs List(1, 2, 3, 4)
//  }
//
//  it should "count all elements without filter" in {
//    createContainer(List(1, 2, 3, 4)).count(_ => true) should be(4)
//  }
//
//  it should "count elements fulfilling filter" in {
//    createContainer(List(1, 2, 3, 4)).count(_ % 2 == 0) should be(2)
//  }
//
//  it should "call reduce" in {
//    createContainer(List(1, 2, 3, 4)).reduce(_ + _) should be(10)
//  }
//
//  it should "call distinct" in {
//    toList(createContainer(List(1, 1, 2, 3, 2, 3, 1)).distinct) should contain theSameElementsAs List(1, 2, 3)
//  }
//
//  it should "call sortBy" in {
//    toList(createContainer(List(3, 5, 1, 8, 4, 6, 2, 9 ,7)))
//      .sortBy(i => i) should contain theSameElementsInOrderAs List(1, 2, 3, 4, 5, 6, 7, 8, 9)
//  }
//
//  it should "call sortBy with reverse ordering" in {
//    toList(createContainer(List(3, 5, 1, 8, 4, 6, 2, 9 ,7))
//      .sortBy(i => i, false)) should contain theSameElementsInOrderAs List(9, 8, 7, 6, 5, 4, 3, 2, 1)
//  }
//
//  it should "call intersect" in {
//    val l1 = createContainer(List(1, 2, 3, 4, 5))
//    val l2 = createContainer(List(3, 4, 5, 6, 7))
//
//    toList(l1.intersection(l2)) should contain theSameElementsAs List(3, 4, 5)
//  }
//
//  it should "calculate catesian product of 2 lists" in {
//    val l1 = createContainer(List(1, 2, 3))
//    val l2 = createContainer(List("a", "b", "c"))
//
//    toList(l1.cartesian(l2)) should contain theSameElementsAs List(
//      (1, "a"),
//      (1, "b"),
//      (1, "c"),
//      (2, "a"),
//      (2, "b"),
//      (2, "c"),
//      (3, "a"),
//      (3, "b"),
//      (3, "c")
//    )
//  }
//
//  it should "call groupBy" in {
//    val grouped = toList(createContainer(List(1, 2, 3, 4, 5, 6)).groupBy(i => i % 2))
//
//    grouped.find(_._1 == 0).value._2 should contain theSameElementsAs List(2, 4, 6)
//    grouped.find(_._1 == 1).value._2 should contain theSameElementsAs List(1, 3, 5)
//  }
//
//  it should "call zip" in {
//    val l1 = createContainer(List(1, 2, 3))
//    val l2 = createContainer(List("a", "b", "c"))
//
//    toList(l1.zip(l2)) should contain theSameElementsAs List((1, "a"), (2, "b"), (3, "c"))
//  }
//
//  it should "call foreach" in {
//    var acc = 0
//    createContainer(List(1, 2, 3)).foreach(i => acc += i)
//
//    acc should be (6)
//  }
//
//  behavior of s"${implicitly[reflect.ClassTag[C[_]]].runtimeClass.getSimpleName} PairContainer"
//
//  it should "reduce by key properly" in {
//    toList(createContainer(List(("a", 1), ("b", 1), ("a", 1), ("c", 1)))
//      .reduceByKey(_ + _)) should contain theSameElementsAs List(("a", 2), ("b", 1), ("c", 1))
//  }
//
//  it should "combineByKey properly" in {
//
//    toList(createContainer(List(("a", 1), ("b", 1), ("a", 1), ("c", 1)))
//      .combineByKey[String](_.toString, (acc, i) => acc + i, _ + _)) should contain theSameElementsAs List(
//      ("a", "11"),
//      ("b", "1"),
//      ("c", "1")
//    )
//  }
//}
