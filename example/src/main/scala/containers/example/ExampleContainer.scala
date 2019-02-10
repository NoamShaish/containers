package containers.example

import containers.Container

import scala.reflect.ClassTag

object ExampleContainer {
  implicit object SetContainer extends Container[Set] {
    override def map[A, B: ClassTag](c: Set[A])(f: (A) => B): Set[B] = c.map(f)

    override def repartition[A](c: Set[A])(repNumber: Int): Set[A] = c

    // $COVERAGE-OFF$Disabling unused example code
    override def flatMap[A, B: ClassTag](c: Set[A])(f: (A) => TraversableOnce[B]): Set[B] = c.flatMap(f)

    override def filter[A](c: Set[A])(p: (A) => Boolean): Set[A] = c.filter(p)

    override def count[A](c: Set[A])(p: (A) => Boolean = (_: A) => true): Long = c.count(p).toLong

    override def reduce[A](c: Set[A])(f: (A, A) => A): A = c.reduce(f)

    override def distinct[A](c: Set[A]): Set[A] = c

    override def union[A](c: Set[A], other: Set[A]): Set[A] = c.union(other)

    override def sortBy[A, K: Ordering : ClassTag](c: Set[A], f: A => K, ascending: Boolean): Set[A] = ???

    override def intersection[A](c: Set[A], other: Set[A]): Set[A] = c.intersect(other)

    override def cartesian[A, B: ClassTag](c: Set[A], other: Set[B]): Set[(A, B)] = ???

    override def groupBy[A, K: ClassTag](c: Set[A], f: A => K): Set[(K, Iterable[A])] = c.groupBy(f).toSet

    override def zip[A, B: ClassTag](c: Set[A], other: Set[B]): Set[(A, B)] = c.zip(other)

    override def foreach[A](c: Set[A], f: A => Unit): Unit = c.foreach(f)
    // $$COVERAGE-ON$
  }

  implicit object VectorContainer extends Container[Vector] {
    override def map[A, B: ClassTag](c: Vector[A])(f: (A) => B): Vector[B] = c.map(f)

    override def repartition[A](c: Vector[A])(repNumber: Int): Vector[A] = c

    // $COVERAGE-OFF$Disabling unused example code
    override def flatMap[A, B: ClassTag](c: Vector[A])(f: (A) => TraversableOnce[B]): Vector[B] = c.flatMap(f)

    override def filter[A](c: Vector[A])(f: (A) => Boolean): Vector[A] = c.filter(f)

    override def count[A](c: Vector[A])(p: (A) => Boolean = (_: A) => true): Long = c.count(p).toLong

    override def reduce[A](c: Vector[A])(f: (A, A) => A): A = c.reduce(f)

    override def distinct[A](c: Vector[A]): Vector[A] = c.distinct

    override def union[A](c: Vector[A], other: Vector[A]): Vector[A] = c.union(other)

    override def sortBy[A, K: Ordering : ClassTag](c: Vector[A], f: A => K, ascending: Boolean): Vector[A] = ???

    override def intersection[A](c: Vector[A], other: Vector[A]): Vector[A] = c.intersect(other)

    override def cartesian[A, B: ClassTag](c: Vector[A], other: Vector[B]): Vector[(A, B)] = ???

    override def groupBy[A, K: ClassTag](c: Vector[A], f: A => K): Vector[(K, Iterable[A])] = c.groupBy(f).toVector

    override def zip[A, B: ClassTag](c: Vector[A], other: Vector[B]): Vector[(A, B)] = c.zip(other)

    override def foreach[A](c: Vector[A], f: A => Unit): Unit = c.foreach(f)
    // $$COVERAGE-ON$
  }
}
