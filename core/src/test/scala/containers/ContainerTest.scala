package containers

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.OptionValues._
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

  it should "call distinct on list" in {
    def distinctIt[C[_]: Container](c: C[Int]): C[Int] = c.distinct

    val l = List(1, 1, 2, 3, 2, 3, 1)

    distinctIt(l) should contain theSameElementsAs List(1, 2, 3)
  }

  it should "call sortBy on list" in {
    def sortByIt[C[_]: Container](c: C[Int])= c.sortBy(i => i)

    val l = List(3, 5, 1, 8, 4, 6, 2, 9 ,7)

    sortByIt(l) should contain theSameElementsInOrderAs List(1, 2, 3, 4, 5, 6, 7, 8, 9)
  }

  it should "call sortBy on list with reverse ordering" in {
    def sortByItDesc[C[_]: Container](c: C[Int])= c.sortBy(i => i, false)

    val l = List(3, 5, 1, 8, 4, 6, 2, 9 ,7)

    sortByItDesc(l) should contain theSameElementsInOrderAs List(9, 8, 7, 6, 5, 4, 3, 2, 1)
  }

  it should "call intersect on list" in {
    def intersectionIt[C[_]: Container](c: C[Int], other: C[Int]): C[Int] = c.intersection(other)

    val l1 = List(1, 2, 3, 4, 5)
    val l2 = List(3, 4, 5, 6, 7)

    intersectionIt(l1, l2) should contain theSameElementsAs List(3, 4, 5)
  }

  it should "calculate catesian product of 2 lists" in {
    val l1 = List(1, 2, 3)
    val l2 = List("a", "b", "c")

    l1.cartesian(l2) should contain theSameElementsAs List(
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

  it should "call groupBy on list" in {
    def groupByIt[C[_]: Container](c: C[Int], f: Int => Int): C[(Int, Iterable[Int])] = c.groupBy(f)

    val l = List(1, 2, 3, 4, 5, 6)

    val grouped = groupByIt(l, i => i % 2)

    grouped.find(_._1 == 0).value._2 should contain theSameElementsAs List(2, 4, 6)
    grouped.find(_._1 == 1).value._2 should contain theSameElementsAs List(1, 3, 5)
  }

  it should "call zip on list" in {
    def zipIt[C[_]: Container](c: C[Int], other: C[String]): C[(Int, String)] = c.zip(other)

    val l1 = List(1, 2, 3)
    val l2 = List("a", "b", "c")

    zipIt(l1, l2) should contain theSameElementsAs List((1, "a"), (2, "b"), (3, "c"))
  }

  it should "call foreach on list" in {
    def foreachIt[C[_]: Container](c: C[Int], f: Int => Unit): Unit = c.foreach(f)

    var acc = 0
    val l = List(1, 2, 3)

    foreachIt(l, i => acc += i)

    acc should be (6)
  }

  behavior of "List PairContainer"

  it should "reduce by key properly" in {
    def sumIt[C[_]: PairContainer](lContainer: C[(String, Int)]): C[(String, Int)] = lContainer.reduceByKey(_ + _)

    val l = List(("a", 1), ("b", 1), ("a", 1), ("c", 1))

    sumIt(l) should contain theSameElementsAs List(("a", 2), ("b", 1), ("c", 1))
  }

  it should "combineByKey properly" in {
    val l = List(("a", 1), ("b", 1), ("a", 1), ("c", 1))

    l.combineByKey[String](_.toString, (acc, i) => acc + i, _ + _) should contain theSameElementsAs List(
      ("a", "11"),
      ("b", "1"),
      ("c", "1")
    )
  }
}
