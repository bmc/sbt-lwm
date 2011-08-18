
name := "sbt-lwm-test"

version := "0.1"

organization := "org.clapper"

scalaVersion := "2.8.1"

libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.0.7"

seq(org.clapper.sbt.lwm.LWM.lwmSettings: _*)

sourceFiles in LWM <++= baseDirectory { d =>
    (d / "src" * "*.txt").get ++
    (d / "src" * "*.md").get ++
    (d / "src" * "*.textile").get
}

cssFile in LWM <<= baseDirectory(d => Some(d / "src" / "style.css" ))

logLevel := Level.Debug

targetDirectory in LWM <<= baseDirectory(_ / "target")

flatten in LWM := true
