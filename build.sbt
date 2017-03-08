// *****************************************************************************
// Projects
// *****************************************************************************

lazy val shufflr =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.akkaHttp,
        library.akkaHttpCirce,
        library.akkaLog4j,
        library.akkaPersistence,
        library.akkaSse,
        library.circeGeneric,
        library.levelDb,
        library.log4jCore,
        library.akkaHttpTestkit % Test,
        library.scalaCheck      % Test,
        library.scalaTest       % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka         = "2.4.17"
      val akkaHttp     = "10.0.4"
      val akkaHttpJson = "1.12.0"
      val akkaLog4j    = "1.3.0"
      val akkaSse      = "2.0.0"
      val circe        = "0.7.0"
      val levelDb      = "0.9"
      val log4j        = "2.8"
      val scala        = "2.12.1"
      val scalaCheck   = "1.13.4"
      val scalaTest    = "3.0.1"
    }
    val akkaHttp        = "com.typesafe.akka"        %% "akka-http"         % Version.akkaHttp
    val akkaHttpCirce   = "de.heikoseeberger"        %% "akka-http-circe"   % Version.akkaHttpJson
    val akkaHttpTestkit = "com.typesafe.akka"        %% "akka-http-testkit" % Version.akkaHttp
    val akkaLog4j       = "de.heikoseeberger"        %% "akka-log4j"        % Version.akkaLog4j
    val akkaPersistence = "com.typesafe.akka"        %% "akka-persistence"  % Version.akka
    val akkaSse         = "de.heikoseeberger"        %% "akka-sse"          % Version.akkaSse
    val circeGeneric    = "io.circe"                 %% "circe-generic"     % Version.circe
    val levelDb         = "org.iq80.leveldb"         %  "leveldb"           % Version.levelDb
    val log4jCore       = "org.apache.logging.log4j" %  "log4j-core"        % Version.log4j
    val scalaCheck      = "org.scalacheck"           %% "scalacheck"        % Version.scalaCheck
    val scalaTest       = "org.scalatest"            %% "scalatest"         % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
  gitSettings ++
  headerSettings

lazy val commonSettings =
  Seq(
    // scalaVersion and crossScalaVersions from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.1",
    // crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "io.shufflr",
    licenses += ("Apache 2.0",
                 url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) +=
      baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    unmanagedSourceDirectories.in(Compile) :=
      Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) :=
      Seq(scalaSource.in(Test).value)
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.HeaderPattern
lazy val headerSettings =
  Seq(
    headers := Map(
      "scala" -> (HeaderPattern.cStyleBlockComment,
                  """|/*
                     | * Copyright 2016 codecentric AG
                     | */
                     |
                     |""".stripMargin)
    )
  )
