---
title: "sbt-lwm: An SBT plugin for editing files"
layout: withTOC
---

# Overview

[sbt-lwm][] (**_L_**ight **_W_**eight **_M_**arkup) is an [SBT][] 0.10.x
plugin that converts lightweight markup documents to HTML. It currently
supports [Textile][] and [Markdown][].

[sbt-lwm][] uses the [MarkWrap][] library to convert the markup to HTML,
so it supports converting:

* [Markdown][], via the [PegDown][] parser.
* [Textile][], via the [FuseSource WikiText fork][] of then Eclipse
  [Mylyn][] *wikitext* parser API.
* Plain text, wrapped in `<pre>` and `</pre>` tags and surrounded by
  HTML goodness.

**This plugin only works with SBT 0.10.x.** If you're using SBT 0.7, there's
an older version (with fewer features and a different variable syntax)
[here](http://software.clapper.org/sbt-plugins/lwm.html).

# Getting the Plugin

First, within your SBT project, create `project/plugins/build.sbt` (if it
doesn't already exist) and add the following:

    // The plugin is only published for 2.8.1 and 2.9.0-1
    libraryDependencies <<= (scalaVersion, libraryDependencies) { (scv, deps) =>
        if ((scv == "2.8.1") || (scv == "2.9.0-1"))
            deps :+ "org.clapper" %% "sbt-lwm" % "0.1.3"
        else
            deps
    }

Next, in your main project `build.sbt` file, add:

    seq(org.clapper.sbt.lwm.LWM.lwmSettings: _*)

Now the plugin is available to your SBT builds.

# Settings and Tasks

The plugin provides the following new settings and tasks.

**Note**: All settings and tasks are in an `LWM` namespace, to avoid import
clashes with identically named settings from other plugins. The pattern for
accessing settings in this plugin is:

    LWM.settingName in LWM.Config <<= ...

Task access is similar.

`LWM` appears twice, which is regrettable; however, until [SBT][] supports
proper namespaces, this appears to be the best way to isolate the plugin's
definitions from clashing with other plugins, when multiple plugins are
used (and, thus, automatically imported) into a `build.sbt` file.

## Settings

---

**`sourceFiles`**

---

The lightweight markup files to be processed. [sbt-lwm][] uses a file's
extension to recognize what kind of lightweight markup the file contains.
The supported extensions are:

* `.md`, `.markdown`: Markdown
* `.textile`: Textile
* `.txt`, `.text,` `.cfg`, `.conf`, `.properties`: Plain text, wrapped in
  a `<pre>` block.

For instance, suppose you want to process all Markdown files within your
"src" tree. You might set `sourceFiles` like this:

    LWM.sourceFiles in LWM.Config <++= baseDirectory(d => (d / "src" ** "*.md").get)

If you also want to apply the edits to all files ending in ".markdown"
(perhaps because you're not consistent in your extensions), use either:

    LWM.sourceFiles in LWM.Config <++= baseDirectory(d => (d / "src" ** "*.md").get)

    LWM.sourceFiles in LWM.Config <++= baseDirectory(d => (d / "src" ** "*.markdown").get)
    
or, more succinctly:

    LWM.sourceFiles in LWM.Config <++= baseDirectory { dir =>
      (dir / "src" ** "*.md").get ++
      (dir / "src" ** "*.markdown").get
    }

### Front Matter

Each source file can optionally start with *front matter*, metadata about
the document. The front matter must be separated from the rest of the
document by a single line like this:

    %%%
    
Front matter consists of one or more *item: value* pairs. Currently, the
only supported item is *title*. To set an individual title for a document,
specify the title in the front matter, like so:

    title: A very cool user's guide
    %%%

    # My User's Guide

If the *title* element is omitted, or if the front-matter is missing, then
the resulting `<title>` HTML element will be empty.

---

**`cssFile`**

---

A [cascading style sheet][] (CSS) file to include, inline, in the `<head>`
section of the HTML document(s). This setting is a Scala `Option` pointing
to a file. By default, no CSS file is included in the generated HTML.

Example:

    LWM.cssFile in LWM.Config <<= baseDirectory(d => Some(d / "src" / "style.css" ))


---

**`targetDirectory`**

---

The directory to which to write the HTML versions of the source files.
For example:

    LWM.targetDirectory in LWM.Config <<= baseDirectory(_ / "target")

See also `flatten`, below.


---

**`flatten`**

---

If `flatten` is `true`, then the processed HTML files will all be placed
directly in `targetDirectory`; if there are name clashes, then some files
will be overwritten. If `flatten` is `false`, then the partial path to each
source file is preserved in the target directory.

An example will help clarify. Consider the following file tree:
 
![Directory tree](tree.png)

Let's assume you're processing all the files ending in ".md", into the *target*
directory.

    LWM.sourceFiles in LWM.Config <++= baseDirectory(d => (d / "src" ** "*.md").get)

    LWM.targetDirectory in LWM.Config <<= baseDirectory(_ / "target")
    
If you also set:

    LWM.flatten in LWM.Config := true

the edit operation will put all the HTML versions of all three files
directly in the *target* directory.

If, instead, you set:

    LWM.flatten in LWM.Config := false

you'll end up with the following edited versions:

* _target/src/main/code/overview.html_
* _target/src/main/code/design.html_
* _target/src/doc/user-guide.html_

---

**`encoding`**

---

The encoding of the source file and, hence, the resulting HTML. Defaults
to "UTF-8".

## Tasks

*sbt-lwm* provides two new SBT tasks.

* `lwm:translate` translates the files specified by `sourceFiles` to HTML.

* `lwm:clean` deletes all generated HTML files. `lwm:clean`
  is also automatically linked into the main SBT `clean` task.

# Change log

The change log for all releases is [here][changelog].

# Author

Brian M. Clapper, [bmc@clapper.org][]

# Copyright and License

This software is copyright &copy; 2011 Brian M. Clapper and is
released under a [BSD License][].

# Patches

I gladly accept patches from their original authors. Feel free to email
patches to me or to fork the [GitHub repository][] and send me a pull
request. Along with any patch you send:

* Please state that the patch is your original work.
* Please indicate that you license the work to the Grizzled-Scala project
  under a [BSD License][].

[BSD License]: license.html
[sbt-lwm web site]: http://software.clapper.org/sbt-lwm/
[sbt-lwm]: http://software.clapper.org/sbt-lwm/
[Markdown]: http://daringfireball.net/projects/markdown/
[MarkWrap]: http://software.clapper.org/markwrap/
[Markdown SBT Plugin]: http://software.clapper.org/sbt-plugins/markdown.html
[Textile]: http://textile.thresholdstate.com/
[SBT]: https://github.com/harrah/xsbt
[GitHub repository]: http://github.com/bmc/sbt-lwm
[GitHub]: https://github.com/bmc/
[bmc@clapper.org]: mailto:bmc@clapper.org
[changelog]: CHANGELOG.html
[PegDown]: http://pegdown.org
[Mylyn]: http://www.eclipse.org/mylyn/
[cascading style sheet]: http://www.w3.org/Style/CSS/
[FuseSource WikiText fork]: https://github.com/fusesource/wikitext
