
name := "sbt-lwm-test"

version := "0.1"

organization := "org.clapper"

libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.0.8"

seq(lwmSettings: _*)

sources in lwmConfig <++= baseDirectory map { d =>
  (d / "src" * "*.txt").get ++
  (d / "src" * "*.md").get ++
  (d / "src" * "*.textile").get
}

lwmCSSFile <<= baseDirectory(d => Some(d / "src" / "style.css" ))

logLevel := Level.Debug

lwmTargetDirectory <<= baseDirectory(_ / "target")

lwmFlatten := true
