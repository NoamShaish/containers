package containers

import scala.annotation.implicitNotFound
import scala.language.higherKinds
import scala.reflect.ClassTag

/**
  * Created by Noam Shaish on 6/25/2017.
  */
@implicitNotFound("No member of type class Container in scope for ${C}")
trait Container[C[_]] {
  def map[A, B: ClassTag](c: C[A])(f: A => B): C[B]

  def flatMap[A, B: ClassTag](c: C[A])(f: A => TraversableOnce[B]): C[B]

  def repartition[A](c: C[A])(repNumber: Int): C[A]

  def filter[A](c: C[A])(p: A => Boolean): C[A]

  def count[A](c: C[A])(p: A => Boolean = (_: A) => true): Long

  def reduce[A](c: C[A])(f: (A, A) => A): A

  def distinct[A](c: C[A]): C[A]

  def union[A](c: C[A], other: C[A]): C[A]

  def sortBy[A, K : Ordering : ClassTag](c: C[A], f: A => K, ascending: Boolean = true): C[A]

  def intersection[A](c: C[A], other: C[A]): C[A]

  def cartesian[A, B: ClassTag](c: C[A], other: C[B]): C[(A, B)]

  def groupBy[A, K: ClassTag](c: C[A], f: A => K): C[(K, Iterable[A])]

  def zip[A, B: ClassTag](c: C[A], other: C[B]): C[(A, B)]

  def foreach[A](c: C[A], f: A => Unit): Unit
}

@implicitNotFound("No member of type class PairContainer in scope for ${C}")
trait PairContainer[C[_]] extends Container[C] {
  def reduceByKey[K: ClassTag, V: ClassTag](c: C[(K, V)])(f: (V, V) => V): C[(K, V)]

  def combineByKey[K: ClassTag, V: ClassTag, B](c: C[(K, V)])(
                       createCombiner: V => B,
                       mergeValue: (B, V) => B,
                       mergeCombiners: (B, B) => B): C[(K, B)]
}

object Container {
  def apply[C[_]: Container]: Container[C] = implicitly[Container[C]]

  def apply[K: ClassTag, V: ClassTag, C[_]: PairContainer]: PairContainer[C] = implicitly[PairContainer[C]]

  object ops {
    implicit class ContainerOps[A, C[_]: Container](c: C[A]) {
      def map[B: ClassTag](f: A => B): C[B] = Container[C].map(c)(f)

      def flatMap[B: ClassTag](f: A => TraversableOnce[B]): C[B] = Container[C].flatMap(c)(f)

      def repartition(repNumber: Int): C[A] = Container[C].repartition(c)(repNumber)

      def filter(p: A => Boolean): C[A] = Container[C].filter(c)(p)

      def count(p: A => Boolean = (_: A) => true): Long = Container[C].count(c)(p)

      def reduce(f: (A, A) => A): A = Container[C].reduce(c)(f)

      def distinct: C[A] = Container[C].distinct(c)

      def union(other: C[A]): C[A] = Container[C].union(c, other)

      def sortBy[K : Ordering : ClassTag](f: A => K, ascending: Boolean = true): C[A] = Container[C].sortBy(c, f, ascending)

      def intersection(other: C[A]): C[A] = Container[C].intersection(c, other)

      def cartesian[B: ClassTag](other: C[B]) = Container[C].cartesian(c, other)

      def groupBy[K: ClassTag](f: A => K): C[(K, Iterable[A])] = Container[C].groupBy(c, f)

      def zip[B: ClassTag](other: C[B]): C[(A, B)] = Container[C].zip(c, other)

      def foreach(f: A => Unit): Unit = Container[C].foreach(c, f)
    }

    implicit class PairContainerOps[K: ClassTag, V: ClassTag, C[_]: PairContainer](c: C[(K, V)]) {
      def reduceByKey(f: (V, V) => V): C[(K, V)] = Container[K, V, C].reduceByKey(c)(f)

      def combineByKey[B](createCombiner: V => B, mergeValue: (B, V) => B, mergeCombiners: (B, B) => B): C[(K, B)] =
        Container[K, V, C].combineByKey(c)(createCombiner, mergeValue, mergeCombiners)
    }
  }

  private sealed class ListContainer extends Container[List] {
    override def map[A, B: ClassTag](c: List[A])(f: (A) => B): List[B] = c.map(f)

    override def repartition[A](c: List[A])(repNumber: Int): List[A] = c

    override def flatMap[A, B: ClassTag](c: List[A])(f: (A) => TraversableOnce[B]): List[B] = c.flatMap(f)

    override def filter[A](c: List[A])(p: (A) => Boolean): List[A] = c.filter(p)

    override def count[A](c: List[A])(p: (A) => Boolean = (_: A) => true): Long = c.count(p).toLong

    override def reduce[A](c: List[A])(f: (A, A) => A): A = c.reduce(f)

    override def distinct[A](c: List[A]): List[A] = c.distinct

    override def union[A](c: List[A], other: List[A]): List[A] = c.union(other)

    override def sortBy[A, K: Ordering : ClassTag](c: List[A], f: A => K, ascending: Boolean): List[A] = c.sortBy(f)(
      if (!ascending) Ordering[K].reverse
      else Ordering[K]
    )

    override def intersection[A](c: List[A], other: List[A]): List[A] = c.intersect(other)

    override def cartesian[A, B: ClassTag](c: List[A], other: List[B]): List[(A, B)] = for {
      l <- c
      r <- other
    } yield (l, r)

    override def groupBy[A, K: ClassTag](c: List[A], f: A => K): List[(K, Iterable[A])] = c.groupBy(f).toList

    override def zip[A, B: ClassTag](c: List[A], other: List[B]): List[(A, B)] = c.zip(other)

    override def foreach[A](c: List[A], f: A => Unit): Unit = c.foreach(f)
  }

  private sealed class PairListContainer extends ListContainer with PairContainer[List] {
    override def reduceByKey[K: ClassTag, V: ClassTag](c: List[(K, V)])(f: (V, V) => V): List[(K, V)] =
      c.groupBy(_._1).map{ case (k, vl) => (k, vl.map(_._2).reduce(f))}.toList

    override def combineByKey[K: ClassTag, V: ClassTag, B](c: List[(K, V)])(
      createCombiner: V => B,
      mergeValue: (B, V) => B,
      mergeCombiners: (B, B) => B): List[(K, B)] =
      c.groupBy(_._1).map{ case (k, vl) => (k, vl.map(v => createCombiner(v._2)).reduce(mergeCombiners))}.toList
  }

  implicit val listContainer: Container[List] = new ListContainer

  implicit val listContainerToPairListContainer: PairContainer[List] = new PairListContainer
}