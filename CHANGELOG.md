---
title: "Change Log: sbt-lwm"
layout: default
---

Version 0.3.2:

* Updated Grizzled Scala and MarkWrap versions.

----

Version 0.3.1:

* Fixed Ivy-related publishing issue.

----

Version 0.3:

* Updated to SBT 0.11.2.
* Added support for *ls.implicit.ly* metadata.

----

Version 0.2.1:

* `LWM.lwmSettings` is now deprecated, in favor of just `LWM.settings`.

----

Version 0.2:

## Change in Setting and Task Key Namespace

*sbt-lwm* setting and task keys are already inside in inner `LWM` object,
for namespace scoping. This revision adds a trick by [Josh Suereth][], to make
usage easier. Basically, the keys are now defined like this:

    object LWM extends Plugin {
      object LWM {
        val Config = config("lwm") extend(Runtime)

        val sources = SettingKey[Seq[File]](
          "source-files", "List of sources to transform"
        ) in Config
    
        val targetDirectory = SettingKey[File](
          "target-directory", "Where to copy edited files"
        ) in Config

        ...
      }
    }

Putting the `in Config` after *each* setting or task changes the `build.sbt`
usage pattern from the clunky

    LWM.sources in LWM <<= ...

to the far more serenely intuitive

    LWM.sources <<= ...

[Josh Suereth]: http://suereth.blogspot.com/

## Changes in Setting Names

`LWM.sourceFiles` is now `LWM.sources`, for consistency with other SBT plugins
and settings.

----

Version 0.1.5:

* Upgraded version of [MarkWrap][], to pick up a change in how line breaks
  in Markdown source are handled.

----

Version 0.1.4:

* Upgraded version of [MarkWrap][], to address problems with HTML entities.
* Also builds for Scala 2.9.1.

[MarkWrap]: http://software.clapper.org/markwrap/

----

Version 0.1.3:

* Converted code to confirm with standard Scala coding style.

* Put plugin settings into a sub-object, so they don't clash with
  other settings on (auto) import into `build.sbt`. Accessing a setting
  is now accomplished with:

        LWM.sourceFiles in LWM.Config <<= baseDirectory(...)

----

Version 0.1.2:

* Now creates a target file's parent directory, if it doesn't already exist.

----

Version 0.1.1:

* Now published for Scala 2.8.1 and 2.9.0-1.

----

Version 0.1:

Completely reimplemented the original [SBT][] 0.7
[Markdown SBT Plugin][] for SBT 0.10.x, switching to the
[MarkWrap][] library for lightweight markup processing.

[MarkWrap]: http://software.clapper.org/markwrap/
[Markdown SBT Plugin]: http://software.clapper.org/sbt-plugins/markdown.html
[SBT]: https://github.com/harrah/xsbt
