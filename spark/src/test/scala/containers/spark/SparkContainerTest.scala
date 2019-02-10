package containers.spark

import org.apache.spark.rdd.RDD
import SparkContainer._
import com.holdenkarau.spark.testing.SharedSparkContext
import containers.ContainerBehaviors

import scala.reflect.ClassTag

class SparkContainerTest extends ContainerBehaviors[RDD] with SharedSparkContext {
  override def createContainer[A: ClassTag](data: List[A]): RDD[A] = sc.parallelize(data)

  override def toList[A: ClassTag](c: RDD[A]): List[A] = c.collect.toList

  it should behave like container

  it should behave like pairContainer
}
