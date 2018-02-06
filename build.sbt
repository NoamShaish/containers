name := "containers"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % "2.1.1",
  "org.scalanlp" % "breeze_2.10" % "0.13.1",
  "org.scalatest" % "scalatest_2.10" % "3.0.1"
)