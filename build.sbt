// ---------------------------------------------------------------------------
// SBT Build File for sbt-lwm Plugin
//
// Copyright (c) 2011-2015 Brian M. Clapper
//
// See accompanying license file for license information.
// ---------------------------------------------------------------------------

import bintray.Keys._

// ---------------------------------------------------------------------------
// Basic settings

name := "sbt-lwm"

version := "0.4.0"

sbtPlugin := true

organization := "org.clapper"

licenses += ("BSD New" -> url("https://github.com/bmc/sbt-lwm/blob/master/LICENSE.md"))

description := "An SBT plugin for processing lightweight markup files"

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

crossScalaVersions := Seq("2.10.4")

seq(lsSettings: _*)

(LsKeys.tags in LsKeys.lsync) := Seq("lwm", "markup", "textile", "markdown")

(description in LsKeys.lsync) <<= description(d => d)

// ---------------------------------------------------------------------------
// Other dependendencies

bintrayResolverSettings

// External deps
libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-scala" % "1.3",
  "org.clapper" %% "markwrap" % "1.0.2"
)

// ---------------------------------------------------------------------------
// Publishing criteria


publishMavenStyle := false

bintrayPublishSettings

repository in bintray := "sbt-plugins"

bintrayOrganization in bintray := None
