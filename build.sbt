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

version := "0.3.3"

sbtPlugin := true

organization := "org.clapper"

licenses := Seq("BSD-like" ->
  url("http://software.clapper.org/sbt-lwm/license.html")
)

description := "An SBT plugin for processing lightweight markup files"

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked")

crossScalaVersions := Seq("2.9.1", "2.9.2")

seq(lsSettings: _*)

(LsKeys.tags in LsKeys.lsync) := Seq("lwm", "markup", "textile", "markdown")

(description in LsKeys.lsync) <<= description(d => d)

// ---------------------------------------------------------------------------
// Other dependendencies

// External deps
libraryDependencies ++= Seq(
    "org.clapper" %% "grizzled-scala" % "1.0.13",
    "org.clapper" %% "markwrap" % "0.5.5"
)

// ---------------------------------------------------------------------------
// Publishing criteria

publishTo <<= (version) { version: String =>
   val scalasbt = "http://scalasbt.artifactoryonline.com/scalasbt/"
   val (name, url) = if (version.contains("-SNAPSHOT"))
                       ("sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
                     else
                       ("sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
   Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}

publishArtifact in packageDoc := false

publishMavenStyle := false

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false
