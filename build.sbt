import _root_.sbt.Keys._
import wartremover.Wart
import wartremover.Wart._

name := "magistracy-homeworks"
version := "0.1"
scalaVersion := "3.2.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test

wartremoverErrors ++= Seq[Wart](Any, AsInstanceOf, Null, Return, Throw, While, MutableDataStructures)
