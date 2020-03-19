name := """tabletop"""
organization := "egfyz29u"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.11"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += javaJdbc
libraryDependencies += javaJpa
libraryDependencies += javaWs
libraryDependencies += play.sbt.PlayImport.cacheApi
libraryDependencies += "com.auth0" % "java-jwt" % "3.3.0"
libraryDependencies += "com.github.karelcemus" %% "play-redis" % "2.6.0"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.4.9.Final"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"