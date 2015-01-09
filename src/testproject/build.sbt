
name := "sbt-lwm-test"

version := "0.2"

organization := "org.clapper"

libraryDependencies += "org.clapper" %% "grizzled-scala" % "1.3"

(sources in LWM) <++= baseDirectory map { d =>
  (d / "src" * "*.txt").get ++
  (d / "src" * "*.md").get ++
  (d / "src" * "*.textile").get
}

cssFile in LWM <<= baseDirectory(d => Some(d / "src" / "style.css" ))

logLevel := Level.Debug

targetDirectory in LWM <<= baseDirectory(_ / "target")

flatten in LWM := true
