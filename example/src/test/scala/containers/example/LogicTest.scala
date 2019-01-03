package containers.example

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{FlatSpec, Matchers}
import containers.spark.SparkContainer._
import ExampleContainer._

/**
  * Created by Noam Shaish on 6/25/2017.
  */
class LogicTest extends FlatSpec with Matchers{
  behavior of "Logic"

  it should "add one to all List members" in {
    Logic.addOne(List(1, 2, 3)) should contain theSameElementsInOrderAs List(2, 3, 4)
  }

  it should "stringfy all List members" in {
    Logic.stringfy(List(1, 2, 3)) should contain theSameElementsInOrderAs List("1", "2", "3")
  }

  it should "add one to all Set members" in {
    Logic.addOne(Set(1, 2, 3)) should contain theSameElementsAs Set(2, 3, 4)
  }

  it should "stingfy all Set members" in {
    Logic.stringfy(Set(1, 2, 3)) should contain theSameElementsAs Set("1", "2", "3")
  }

  it should "add one to all Vector members" in {
    Logic.addOne(Vector(1, 2, 3)) should contain theSameElementsInOrderAs Vector(2, 3, 4)
  }

  it should "stringfy all Vector members" in {
    Logic.stringfy(Vector(1, 2, 3)) should contain theSameElementsInOrderAs Vector("1", "2", "3")
  }

  it should "work with RDD" in {
    val sc = new SparkContext(new SparkConf().setAppName("Container test").setMaster("local[2]"))
    val rdd = sc.parallelize(Seq(1, 2, 3))
    Logic.addOne(rdd).collect() should contain theSameElementsAs Seq(2, 3, 4)
    Logic.stringfy(rdd).collect() should contain theSameElementsAs Seq("1", "2", "3")
    sc.stop()
  }

  it should "word count List" in {
    Logic.wordCount(List("Hello world", "Goodbye world")) should contain theSameElementsAs List(("Hello", 1), ("Goodbye", 1), ("world", 2))
  }

  it should "word count RDD" in {
    val sc = new SparkContext(new SparkConf().setAppName("Container test").setMaster("local[2]"))
    Logic.wordCount(sc.parallelize(Seq("Hello world", "Goodbye world"))).collect() should contain theSameElementsAs Seq(("Hello", 1), ("Goodbye", 1), ("world", 2))
    sc.stop()
  }

  it should "estimate PI on List" in {
    val sampleCount = 10000
    val estimation = Logic.PIEstimation((1 to sampleCount).map(_.toLong).toList, sampleCount.toLong)
    estimation should be > 3.1
    estimation should be < 3.2
  }

  it should "estimate PI on RDD" in {
    val sc = new SparkContext(new SparkConf().setAppName("Container test").setMaster("local[2]"))

    val sampleCount = 100000
    val estimation = Logic.PIEstimation(sc.parallelize((1 to sampleCount).map(_.toLong).toList), sampleCount.toLong)
    estimation should be > 3.1
    estimation should be < 3.2
    sc.stop()
  }

  it should "perform logistic regression based classification on List" in {
    Logic.logisticRegressionBasedClassification(Logic.generateData().toList)
  }

  it should "perform logistic regression based classification on RDD" in {
    val sc = new SparkContext(new SparkConf().setAppName("Container test").setMaster("local[2]"))
    Logic.logisticRegressionBasedClassification(sc.parallelize(Logic.generateData()))
    sc.stop()
  }
}
