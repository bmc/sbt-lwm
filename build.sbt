// ---------------------------------------------------------------------------
// SBT Build File for sbt-lwm Plugin
//
// Copyright (c) 2011-2018 Brian M. Clapper
//
// See accompanying license file for license information.
// ---------------------------------------------------------------------------

lazy val commonSettings = Seq(
  version in ThisBuild := "1.0.0",
  organization in ThisBuild := "org.clapper"
)

lazy val root = (project in file(".")).settings(
  commonSettings,

  sbtPlugin := true,
  name := "sbt-lwm",
  description := "An SBT plugin for processing lightweight markup files",
  licenses += ("BSD New" -> url("https://github.com/bmc/sbt-lwm/blob/master/LICENSE.md")),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization in bintray := None,

  scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
  // Note: To cross-build, use "^compile", not "+compile", and
  // "^publishLocalSigned" or "^publish"
  crossSbtVersions := Seq("0.13.16", "1.0.3"),
  libraryDependencies ++= Seq("org.clapper" %% "markwrap" % "1.1.2")
)
