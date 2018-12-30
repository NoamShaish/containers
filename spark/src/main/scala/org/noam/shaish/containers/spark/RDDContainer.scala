package org.noam.shaish.containers.spark

import org.apache.spark.rdd.RDD
import org.noam.shaish.containers.{Container, PairContainer}

import scala.reflect.ClassTag

object RDDContainer {
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
