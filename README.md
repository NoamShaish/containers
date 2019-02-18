# Containers
[![Project License][licence_icon]][licence_link]
[![Travis Status][travis_icon]][travis_link]
[![Coverage Status][coverage_icon]][coverage_link]
[![Nexus Release][nexus_icon]][nexus_link]


[licence_icon]: https://img.shields.io/github/license/NoamShaish/containers.svg
[licence_link]: http://www.apache.org/licenses/

[travis_icon]: https://travis-ci.com/NoamShaish/containers.svg?branch=master
[travis_link]: https://travis-ci.com/NoamShaish/containers

[coverage_icon]: https://coveralls.io/repos/github/NoamShaish/containers/badge.svg?branch=master
[coverage_link]: https://coveralls.io/github/NoamShaish/containers?branch=master

[nexus_icon]: https://img.shields.io/nexus/r/https/oss.sonatype.org/com.github.NoamShaish/containers-core_2.11.svg
[nexus_link]: https://oss.sonatype.org/#nexus-search;quick~containers-core_2.11

## What is a Container?
Container is yet another collection abstraction.

Its implemented simply by a type class so you can happily extend it to what ever propose you need.

## Why Containers?
Containers initial propose was to be able to write logic that can run on lists as spark RDD's.
And to be able to test distributed logic without the burden of spinning up spark context.

But after using them they turned to be useful to compare different computation engines,
like scalding, flink etc. it was easy to just implement the fitting type class.


## Getting Containers

If you are using SBT, add the following to your `build.sbt`:
```scala
libraryDependencies += "com.github.NoamShaish" %% "containers-core" % "0.1.0"
```

If you would like to use the RDD container, add the following to your `build.sbt`:
```
libraryDependencies += "com.github.NoamShaish" %% "containers-core" % "0.1.0"
```

## Quick Start
```scala
import containers.Container
import containers.Container.ops._

scala> def mapIt[C[_]: Container](c: C[Int]) = c.map(_ + 1)
mapIt: [C[_]](c: C[Int])(implicit evidence$1: containers.Container[C])C[Int]

scala> mapIt(List(1, 2, 3, 4))
res0: List[Int] = List(2, 3, 4, 5)
```

Not really impressive, but the same "logic" can run on an RDD
```scala
scala> mapIt(sc.parallelize(List(1, 2, 3, 4)).collect
res1: Array[Int] = Array(2, 3, 4, 5)
```

### Examples
In containers-example you can see a more impressive examples such as:
- PI Estimation
- Logistic Regression Based Classification

both can run on either list or RDD with the exact same code.

### Release
```
sbt
> set version in ThisBuild := "x.x.x"
> publishSigned
> sonatypeRelease
```
