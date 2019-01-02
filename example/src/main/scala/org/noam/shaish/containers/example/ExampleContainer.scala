package org.noam.shaish.containers.example

import org.noam.shaish.containers.Container

import scala.reflect.ClassTag

object ExampleContainer {
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
}
