/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2011, Brian M. Clapper
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

import scala.io.Source
import scala.annotation.tailrec

class Document(source: Source) {
  private val (front, doc) = splitSource(source)
  val frontMatter: Map[String,String] = parseFrontMatter(front)
  val content = doc mkString "\n"

  // -----------------------------------------------------------------
  // Public Methods
  // -----------------------------------------------------------------

  def contentSource = Source.fromString(content)

  // -----------------------------------------------------------------
  // Private Utility Methods
  // -----------------------------------------------------------------

  private def parseFrontMatter(lines: List[String]): Map[String, String] = {
    val re = """^([^:\s]+)\s*:\s*(.*)$""".r

    def oops(line: String) =
      throw new Exception("Bad front matter line: " + line)

    Map[String, String]() ++ lines.map {line => 
      re.findFirstMatchIn(line).map {m => (m.group(1), m.group(2))}.
         getOrElse(oops(line))
    }
  }

  private def splitSource(source: Source): (List[String], List[String]) = {
    val lines = source.getLines.toList
    val i = lines.indexOf("%%%")

    if (i == -1)
      (Nil, lines)
    else {
      val (before, after) = lines.splitAt(i)
      (before, after drop 1)
    }
  }
}
