package org.noam.shaish.containers.example

import java.util.Random

import breeze.linalg.{DenseVector, Vector}
import org.noam.shaish.containers.{Container, PairContainer}
import org.noam.shaish.containers.Container.ops._

import scala.language.higherKinds

/**
  * Created by Noam Shaish on 6/25/2017.
  */
object Logic {

  def addOne[C[_]: Container](c: C[Int]): C[Int] = c.map((i: Int) => i + 1)

  def stringfy[C[_]: Container](c: C[Int]): C[String] = c.map((i: Int) => i.toString)

  def wordCount[C[_]: PairContainer](lines: C[String]): C[(String, Long)] =
    lines.flatMap((s: String) => s.split(" ")).map(word => (word, 1l)).reduceByKey(_ + _)

  def PIEstimation[C[_]: Container](samples: C[Long], numberOfSamples: Long): Double = {
    val count = samples.count { _ =>
      val x = math.random
      val y = math.random
      x*x + y*y < 1
    }
    4.0 * count / numberOfSamples
  }

  private val rand = new Random(42)

  case class DataPoint(x: Vector[Double], y: Double)

  def generateData(dimensions: Int = 10, scalingFactor: Double = 0.7, numDataPoints: Int = 10000): Array[DataPoint] = {
    def generatePoint(i: Int): DataPoint = {
      val y = if (i % 2 == 0) -1 else 1
      val x = DenseVector.fill(dimensions) {rand.nextGaussian + y * scalingFactor}
      DataPoint(x, y.toDouble)
    }
    Array.tabulate(numDataPoints)(generatePoint)
  }

  def logisticRegressionBasedClassification[C[_] : Container](points: C[DataPoint], dimensions: Int = 10, iterations: Int = 5): DenseVector[Double] = {
    // Initialize w to a random value
    var w = DenseVector.fill(dimensions) {2 * rand.nextDouble - 1}
    println("Initial w: " + w)

    for (i <- 1 to iterations) {
      val gradient = points.map { p =>
        p.x * (1 / (1 + math.exp(-p.y * w.dot(p.x))) - 1) * p.y
      }.reduce(_ + _)
      w -= gradient
    }

    println("Final w: " + w)
    w
  }
}
