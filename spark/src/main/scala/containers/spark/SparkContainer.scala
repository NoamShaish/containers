package containers.spark

import containers.{Container, PairContainer}
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object SparkContainer {
  private sealed class RDDContainer extends Container[RDD] {
    override def map[A, B: ClassTag](c: RDD[A])(f: (A) => B): RDD[B] = c.map(f)

    override def repartition[A](c: RDD[A])(repNumber: Int): RDD[A] = c.repartition(repNumber)

    override def flatMap[A, B: ClassTag](c: RDD[A])(f: (A) => TraversableOnce[B]): RDD[B] = c.flatMap(f)

    override def filter[A](c: RDD[A])(f: (A) => Boolean): RDD[A] = c.filter(f)

    override def count[A](c: RDD[A])(p: (A) => Boolean = (_: A) => true): Long = c.filter(p).count()

    override def reduce[A](c: RDD[A])(f: (A, A) => A): A = c.reduce(f)

    override def distinct[A](c: RDD[A]): RDD[A] = c.distinct

    override def union[A](c: RDD[A], other: RDD[A]): RDD[A] = c.union(other)

    override def sortBy[A, K: Ordering : ClassTag](c: RDD[A], f: A => K, ascending: Boolean): RDD[A] = c.sortBy(f, ascending)

    override def intersection[A](c: RDD[A], other: RDD[A]): RDD[A] = c.intersection(other)

    override def cartesian[A, B: ClassTag](c: RDD[A], other: RDD[B]): RDD[(A, B)] = c.cartesian(other)

    override def groupBy[A, K: ClassTag](c: RDD[A], f: A => K): RDD[(K, Iterable[A])] = c.groupBy(f)

    override def zip[A, B: ClassTag](c: RDD[A], other: RDD[B]): RDD[(A, B)] = c.zip(other)

    override def foreach[A](c: RDD[A], f: A => Unit): Unit = c.foreach(f)
  }

  private sealed class PairRDDContainer extends RDDContainer with PairContainer[RDD]{
    override def reduceByKey[K: ClassTag, V: ClassTag](c: RDD[(K, V)])(f: (V, V) => V): RDD[(K, V)] = c.reduceByKey(f)

    override def combineByKey[K: ClassTag, V: ClassTag, B](c: RDD[(K, V)])(
      createCombiner: V => B,
      mergeValue: (B, V) => B,
      mergeCombiners: (B, B) => B): RDD[(K, B)] = c.combineByKey(createCombiner, mergeValue, mergeCombiners)
  }

  implicit val rddContainer: Container[RDD] = new RDDContainer

  implicit val pairRDDContainer: PairContainer[RDD] = new PairRDDContainer
}
