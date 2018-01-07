
name := "sbt-lwm-test"

version := "0.3"

organization := "org.clapper"

(sources in LWM) ++= (baseDirectory.value / "src" * "*.txt").get ++
                     (baseDirectory.value / "src" * "*.md").get ++
                     (baseDirectory.value / "src" * "*.textile").get

cssFile in LWM := Some(baseDirectory.value / "src" / "style.css")

logLevel := Level.Debug

targetDirectory in LWM := baseDirectory.value / "target"

flatten in LWM := true

compile in Compile := ((compile in Compile) dependsOn (translate in LWM)).value
