/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2011-2012, Brian M. Clapper
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  * Neither the names "clapper.org", "sbt-lwm", nor the names of any
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  ---------------------------------------------------------------------------
*/

package org.clapper.sbt.lwm

import sbt._
import Keys._
import Project.Initialize

import java.io.{File, FileWriter, PrintWriter}

import scala.io.Source
import scala.util.matching.Regex
import scala.annotation.tailrec

import grizzled.file.{util => FileUtil}

/**
 * Plugin for SBT (Simple Build Tool) that provides a task to convert
 * files in several lightweight markup languages (Textile, Markdown)
 * to HTML.
 */
object LWM extends Plugin {
  import LWMKeys._

  // -----------------------------------------------------------------
  // Constants
  // -----------------------------------------------------------------

  private val ExtensionPattern = """^(.*)\.[^.]+$""".r

  // -----------------------------------------------------------------
  // Plugin Settings and Task Declarations
  // -----------------------------------------------------------------

  object LWM {
    val Config = config("lwm") extend(Runtime)

    // targetDirectory is the directory where HTML files are to be
    // written.
    val targetDirectory = SettingKey[File](
      "target-directory", "Where to copy generated HTML"
    )

    // Optional CSS file to include with the translated documents.
    val cssFile = SettingKey[Option[File]](
      "css", "CSS file to insert, inline into generated HTML"
    )

    val encoding = SettingKey[String](
      "encoding", "document encoding"
    )

    // Whether or not to flatten the directory structure.
    val flatten = SettingKey[Boolean](
      "flatten", "Don't preserve source directory structure."
    )

    val translate = TaskKey[Unit]("translate", "Translate the docs")
    val lwmClean = TaskKey[Unit]("clean", "Remove target files.")

    val settings: Seq[sbt.Project.Setting[_]] = inConfig(Config)(Seq(

      flatten := true,
      encoding := "UTF-8",
      cssFile := None,
      sources := Seq.empty[File],

      targetDirectory <<= baseDirectory(_ / "target"),

      translate <<= translateTask,
      clean in Config <<= cleanTask
    )) ++
    inConfig(Compile)(Seq(
      // Hook our clean into the global one.
      clean in Global <<= clean in LWM.Config
    ))
  }

  // -----------------------------------------------------------------
  // Public Methods
  // -----------------------------------------------------------------

  // -----------------------------------------------------------------
  // Task Implementations
  // -----------------------------------------------------------------

  private def cleanTask: Initialize[Task[Unit]] = {
    (sources in LWM.Config, LWM.targetDirectory, baseDirectory, LWM.flatten,
     streams) map  {
      (sourceFiles, targetDirectory, baseDirectory, flatten, streams) =>

      for (sourceFile <- sourceFiles) {
        val targetFile = targetFor(sourceFile,
                                   targetDirectory,
                                   baseDirectory,
                                   flatten)
        if (targetFile.exists) {
          streams.log.debug("Deleting \"%s\"" format targetFile)
          targetFile.delete
        }
      }
    }
  }

  private def translateTask: Initialize[Task[Unit]] = {
    (sources in LWM.Config, LWM.targetDirectory, baseDirectory, LWM.flatten,
     LWM.cssFile, LWM.encoding, streams) map {
      (sources, target, base, flatten, cssFile, encoding, streams) =>

      val css = cssFile.map {
        f => Source.fromFile(f).getLines.mkString("\n")
      }
      sources map translateSource(target, base, css, flatten,
                                  encoding, streams.log)
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
                             (sourceFile: File): Unit = {
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
    val cssSource = css map {Source.fromString(_)}
    val html = parser.parseToHTMLDocument(document.contentSource,
                                          title, cssSource, encoding)
    val parentDir = new File(target.getParent)
    if ((parentDir.exists) && (! parentDir.isDirectory)) {
      throw new Exception("Target directory \"%s\" of file \"%s\" " +
                          "exists, but is not a directory."
                          format (parentDir, target))
    }

    if (! parentDir.exists) {
      if (! parentDir.mkdirs()) {
        throw new Exception("Cannot make parent directory \"%s\" " +
                            "of file \"%s\"." format
                            (parentDir, target))
      }
    }
    

    val out = new FileWriter(target)
    out.write(html)
    out.close()
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
                            "\"%s\", since it isn't under the base " +
                            "directory \"%s\""
                            format (sourcePath, basePath))
      }

      Path(targetDirectory) / zapExtension(sourcePath.drop(basePath.length))
    }
  }
}
