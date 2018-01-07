/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2011-2018, Brian M. Clapper
  All rights reserved.

  See accompanying license file for license information.
  ---------------------------------------------------------------------------
*/

package org.clapper.sbt.lwm

import sbt._
import Keys._

import java.io.{File, FileWriter}

import scala.io.Source

/**
 * Plugin for SBT (Simple Build Tool) that provides a task to convert
 * files in several lightweight markup languages (Textile, Markdown)
 * to HTML.
 */
object LWMPlugin extends AutoPlugin {

  // -----------------------------------------------------------------
  // Constants
  // -----------------------------------------------------------------

  private val ExtensionPattern = """^(.*)\.[^.]+$""".r

  // -----------------------------------------------------------------
  // Plugin Settings and Task Declarations
  // -----------------------------------------------------------------

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {
    val LWM = config("lwm")

    // targetDirectory is the directory where HTML files are to be
    // written.
    val targetDirectory = settingKey[File]("Where to copy generated HTML")

    // Optional CSS file to include with the translated documents.
    val cssFile = settingKey[Option[File]](
      "CSS file to insert inline into generated HTML"
    )

    val encoding = settingKey[String]("Document encoding")

    // Whether or not to flatten the directory structure.
    val flatten = settingKey[Boolean](
      "Don't preserve source directory structure."
    )

    val translate = TaskKey[Unit]("translate", "Translate the docs")
    val lwmClean = TaskKey[Unit]("clean", "Remove target files.")

    lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
      flatten           := true,
      encoding          := "UTF-8",
      cssFile           := None,
      sources           := Seq.empty[File],
      targetDirectory   := baseDirectory(_ / "target").value,

      translate in LWM  := LWMRunner.translate(LWM).value,
      clean in LWM      := LWMRunner.clean(LWM).value
    )
  }

  import autoImport._

  override lazy val projectSettings = inConfig(LWM)(baseSettings)

  object LWMRunner {
    def translate(config: Configuration): Def.Initialize[Task[Seq[File]]] = Def.task {
      val sourceFiles = (sources in LWM).value
      val targetDir   = (targetDirectory in LWM).value
      val flattenTree = (flatten in LWM).value
      val baseDir     = baseDirectory.value
      val enc         = (encoding in LWM).value
      val log         = streams.value.log

      // Get the CSS contents, if any.
      val css = (cssFile in LWM).value.map { Source.fromFile(_).mkString }

      sourceFiles map translateSource(targetDir, baseDir, css, flattenTree,
                                      enc, log)
    }

    def clean(config: Configuration): Def.Initialize[Task[Unit]] = Def.task {
      val sourceFiles = (sources in LWM).value
      val targetDir   = (targetDirectory in LWM).value
      val flattenTree = (flatten in LWM).value
      val baseDir     = baseDirectory.value
      val log         = streams.value.log

      for (src <- sourceFiles) {
        val targetFile = targetFor(src, targetDir, baseDir, flattenTree)
        if (targetFile.exists) {
          log.debug(s"""Deleting "$targetFile"""")
          targetFile.delete
        }
      }
    }
  }

  // -----------------------------------------------------------------
  // Private Utility Methods
  // -----------------------------------------------------------------

  private def translateSource(targetDirectory: File,
                              baseDirectory: File,
                              css: Option[String],
                              flatten: Boolean,
                              encoding: String,
                              log: Logger)
                             (sourceFile: File): File = {
    import org.clapper.markwrap.MarkWrap

    val parser = MarkWrap.parserFor(sourceFile)
    val target = targetFor(sourceFile,
                           targetDirectory,
                           baseDirectory,
                           flatten)
    log.info("Translating %s document \"%s\" to \"%s\"\n"
             format (parser.markupType.name, sourceFile, target))

    val document = new Document(Source.fromFile(sourceFile))
    val title = document.frontMatter.getOrElse("title", "")
    val cssSource = css map Source.fromString
    val html = parser.parseToHTMLDocument(document.contentSource,
                                          title, cssSource, encoding)
    val parentDir = new File(target.getParent)
    if (parentDir.exists && (! parentDir.isDirectory)) {
      throw new Exception(s"""Target directory "$parentDir" of file """ +
                          s""""$target" exists, but is not a directory.""")
    }

    if (! parentDir.exists) {
      if (! parentDir.mkdirs()) {
        throw new Exception(s"""Cannot make parent directory "$parentDir """ +
                            s"""of file "$target".""")
      }
    }


    val out = new FileWriter(target)
    out.write(html)
    out.close()

    target
  }

  private def zapExtension(path: String): String =
    ExtensionPattern.replaceFirstIn(path, "$1")

  private def targetFor(sourceFile: File,
                        targetDirectory: File,
                        baseDirectory: File,
                        flatten: Boolean = true): File = {
    if (flatten) {
      Path(targetDirectory) / (sourceFile.base + ".html")
    }

    else {
      val sourcePath = sourceFile.absolutePath
      val targetDirPath = targetDirectory.absolutePath
      val basePath = baseDirectory.absolutePath
      if (! sourcePath.startsWith(basePath)) {
        throw new Exception("Can't preserve directory structure for " +
                            s""""$sourcePath", since it isn't under the """ +
                            s"""base directory "$basePath".""")
      }

      Path(targetDirectory) / zapExtension(sourcePath.drop(basePath.length))
    }
  }
}
