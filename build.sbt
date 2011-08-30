// ---------------------------------------------------------------------------
// SBT 0.10.x Build File for sbt-lwm Plugin
//
// Copyright (c) 2011 Brian M. Clapper
//
// See accompanying license file for license information.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// Basic settings

name := "sbt-lwm"

version := "0.1.3"

sbtPlugin := true

organization := "org.clapper"

scalaVersion := "2.8.1"

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked")

crossScalaVersions := Seq("2.8.1", "2.9.0-1")

// ---------------------------------------------------------------------------
// Posterous-SBT

libraryDependencies <<= (sbtVersion, scalaVersion, libraryDependencies) { (sbtv, scalav, deps) =>
    if (scalav == "2.8.1")
        deps :+ "net.databinder" %% "posterous-sbt" % ("0.3.0_sbt" + sbtv)
    else
        deps
}

(name in Posterous) := "sbt-lwm"

// ---------------------------------------------------------------------------
// Other dependendencies

// External deps
libraryDependencies ++= Seq(
    "org.clapper" %% "grizzled-scala" % "1.0.7",
    "org.clapper" %% "markwrap" % "0.4.1"
)

// ---------------------------------------------------------------------------
// Publishing criteria

publishTo <<= version {(v: String) =>
    val nexus = "http://nexus.scala-tools.org/content/repositories/"
    if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots/") 
    else                             Some("releases"  at nexus + "releases/")
}

publishMavenStyle := true

publishArtifact in packageDoc := false

credentials += Credentials(Path.userHome / "src" / "mystuff" / "scala" /
                           "nexus.scala-tools.org.properties")

