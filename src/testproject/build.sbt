
name := "sbt-lwm-test"

version := "0.1"

organization := "org.clapper"

libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.0.10"

seq(LWM.settings: _*)

sources in LWM.Config <++= baseDirectory map { d =>
  (d / "src" * "*.txt").get ++
  (d / "src" * "*.md").get ++
  (d / "src" * "*.textile").get
}

LWM.cssFile in LWM.Config <<= baseDirectory(d => Some(d / "src" / "style.css" ))

logLevel := Level.Debug

LWM.targetDirectory in LWM.Config <<= baseDirectory(_ / "target")

LWM.flatten in LWM.Config := true
