package noam.shaish.containers
import org.apache.spark.rdd.RDD

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
}

@implicitNotFound("No member of type class PairContainer in scope for ${C}")
trait PairContainer[C[_]] extends Container[C] {
  def reduceByKey[K: ClassTag, V: ClassTag](c: C[(K, V)])(f: (V, V) => V): C[(K, V)]
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
    }

    implicit class PairContainerOps[K: ClassTag, V: ClassTag, C[_]: PairContainer](c: C[(K, V)]) {
      def reduceByKey(f: (V, V) => V): C[(K, V)] = Container[K, V, C].reduceByKey(c)(f)
    }
  }

  private sealed class ListContainer extends Container[List] {
    override def map[A, B: ClassTag](c: List[A])(f: (A) => B): List[B] = c.map(f)

    override def repartition[A](c: List[A])(repNumber: Int): List[A] = c

    override def flatMap[A, B: ClassTag](c: List[A])(f: (A) => TraversableOnce[B]): List[B] = c.flatMap(f)

    override def filter[A](c: List[A])(p: (A) => Boolean): List[A] = c.filter(p)

    override def count[A](c: List[A])(p: (A) => Boolean = (_: A) => true): Long = c.count(p).toLong

    override def reduce[A](c: List[A])(f: (A, A) => A): A = c.reduce(f)
  }

  private sealed class PairListContainer extends ListContainer with PairContainer[List] {
    override def reduceByKey[K: ClassTag, V: ClassTag](c: List[(K, V)])(f: (V, V) => V): List[(K, V)] =
      c.groupBy(_._1).map{ case (k, vl) => (k, vl.map(_._2).reduce(f))}.toList
  }

  implicit val listContainer: Container[List] = new ListContainer

  implicit val listContainerToPairListContainer: PairContainer[List] = new PairListContainer

  implicit object SetContainer extends Container[Set] {
    override def map[A, B: ClassTag](c: Set[A])(f: (A) => B): Set[B] = c.map(f)

    override def repartition[A](c: Set[A])(repNumber: Int): Set[A] = c

    override def flatMap[A, B: ClassTag](c: Set[A])(f: (A) => TraversableOnce[B]): Set[B] = c.flatMap(f)

    override def filter[A](c: Set[A])(p: (A) => Boolean): Set[A] = c.filter(p)

    override def count[A](c: Set[A])(p: (A) => Boolean = (_: A) => true): Long = c.count(p).toLong

    override def reduce[A](c: Set[A])(f: (A, A) => A): A = c.reduce(f)
  }

  implicit object VectorContainer extends Container[Vector] {
    override def map[A, B: ClassTag](c: Vector[A])(f: (A) => B): Vector[B] = c.map(f)

    override def repartition[A](c: Vector[A])(repNumber: Int): Vector[A] = c

    override def flatMap[A, B: ClassTag](c: Vector[A])(f: (A) => TraversableOnce[B]): Vector[B] = c.flatMap(f)

    override def filter[A](c: Vector[A])(f: (A) => Boolean): Vector[A] = c.filter(f)

    override def count[A](c: Vector[A])(p: (A) => Boolean = (_: A) => true): Long = c.count(p).toLong

    override def reduce[A](c: Vector[A])(f: (A, A) => A): A = c.reduce(f)
  }

  private sealed class RDDContainer extends Container[RDD] {
    override def map[A, B: ClassTag](c: RDD[A])(f: (A) => B): RDD[B] = c.map(f)

    override def repartition[A](c: RDD[A])(repNumber: Int): RDD[A] = c.repartition(repNumber)

    override def flatMap[A, B: ClassTag](c: RDD[A])(f: (A) => TraversableOnce[B]): RDD[B] = c.flatMap(f)

    override def filter[A](c: RDD[A])(f: (A) => Boolean): RDD[A] = c.filter(f)

    override def count[A](c: RDD[A])(p: (A) => Boolean = (_: A) => true): Long = c.filter(p).count()

    override def reduce[A](c: RDD[A])(f: (A, A) => A): A = c.reduce(f)
  }

  private sealed class PairRDDContainer extends RDDContainer with PairContainer[RDD]{
    override def reduceByKey[K: ClassTag, V: ClassTag](c: RDD[(K, V)])(f: (V, V) => V): RDD[(K, V)] = c.reduceByKey(f)
  }

  implicit val rddContainer: Container[RDD] = new RDDContainer

  implicit val pairRDDContainer: PairContainer[RDD] = new PairRDDContainer
}