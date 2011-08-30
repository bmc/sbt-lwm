---
title: "Change Log: sbt-lwm"
layout: default
---

Version 0.1.3:

* Converted code to confirm with standard Scala coding style.

* Put plugin settings into a sub-object, so they don't clash with
  other settings on (auto) import into `build.sbt`. Accessing a setting
  is now accomplished with:

        LWM.sourceFiles in LWM.Config <<= baseDirectory(...)

Version 0.1.2:

* Now creates a target file's parent directory, if it doesn't already exist.

Version 0.1.1:

* Now published for Scala 2.8.1 and 2.9.0-1.

Version 0.1:

Completely reimplemented the original [SBT][] 0.7
[Markdown SBT Plugin][] for SBT 0.10.x, switching to the
[MarkWrap][] library for lightweight markup processing.

[MarkWrap]: http://software.clapper.org/markwrap/
[Markdown SBT Plugin]: http://software.clapper.org/sbt-plugins/markdown.html
[SBT]: https://github.com/harrah/xsbt
