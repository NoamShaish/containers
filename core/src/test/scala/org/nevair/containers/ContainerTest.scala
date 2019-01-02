package org.nevair.containers

import org.scalatest.{FlatSpec, Matchers}
import Container.ops._

import scala.language.higherKinds

class ContainerTest extends FlatSpec with Matchers {
  behavior of "List Container"

  it should "call map on list" in {
    def mapIt[C[_]: Container](lContainer: C[Int])(f: Int => String): C[String] = lContainer.map(f)

    val l = List(1, 2, 3, 4)

    mapIt(l)(_.toString) should contain theSameElementsInOrderAs l.map(_.toString)
  }

  it should "call flatMap on list" in {
    def flatMapIt[C[_]: Container](lContainer: C[(Int, Int)]): C[Int] = lContainer.flatMap(t => Seq(t._1, t._2))

    val l = List((1, 2), (3, 4))

    flatMapIt(l) should contain theSameElementsInOrderAs l.flatMap(t => Seq(t._1, t._2))
  }

  it should "do nothing when calling repartition on list" in {
    def repartitionIt[C[_]: Container](lContainer: C[Int]): C[Int] = lContainer.repartition(1000)

    val l = List(1, 2, 3, 4)

    repartitionIt(l) should contain theSameElementsInOrderAs l
  }

  it should "count all elements without filter" in {
    def countItAll[C[_]: Container](lContainer: C[Int]): Long = lContainer.count(_ => true)

    val l = List(1, 2, 3, 4)

    countItAll(l) should be(l.size)
  }

  it should "count elements fulfilling filter" in {
    def countIt[C[_]: Container](lContainer: C[Int])(p: Int => Boolean): Long = lContainer.count(p)

    val l = List(1, 2, 3, 4)

    countIt(l)(_ % 2 == 0) should be(l.count(_ % 2 == 0))
  }

  it should "call reduce on list" in {
    def reduceIt[C[_]: Container](lContainer: C[Int])(f: (Int, Int) => Int): Int = lContainer.reduce(f)

    val l = List(1, 2, 3, 4)

    reduceIt(l)(_ + _) should be(l.sum)
  }

  behavior of "List PairContainer"

  it should "reduce by key properly" in {
    def sumIt[C[_]: PairContainer](lContainer: C[(String, Int)]): C[(String, Int)] = lContainer.reduceByKey(_ + _)

    val l = List(("a", 1), ("b", 1), ("a", 1), ("c", 1))

    sumIt(l) should contain theSameElementsAs List(("a", 2), ("b", 1), ("c", 1))
  }
}
