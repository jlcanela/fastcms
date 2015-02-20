/**
 *  Copyright 2014 Reverb Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package models

import java.io.{FilenameFilter, FileNotFoundException, FileInputStream, File}
import java.net.URL
import java.nio.file.{PathMatcher, SimpleFileVisitor, Path, FileSystems}

import org.zeroturnaround.zip.ZipUtil
import play.Play
import play.api.libs.json.Json
import play.api.{Logger, Configuration}


import scalaz.\/

case class Website(id: Int, name: String, url: String, port: Int, path: String) {

  def log(rootLogPath: File) = new File(rootLogPath, s"$name")
  def www(rootDataPath: File) = path match {
    case "" => new File(rootDataPath, s"www/$name")
    case p => new File(new File(rootDataPath, ".."), p)
  } 

  /**
   * fetch the file and unzip it to path
   * @param path
   */
  def fetchContent(target: File) : \/[String, Unit]= try {
    val asUrl = new URL(url)
    val stream = asUrl.getProtocol match {
      case "file" => new FileInputStream(asUrl.getFile)
      case _ => new URL(url).openStream()
    }

    ZipUtil.unpack(stream, target)
    if (target.exists()) {
      \/.right(())
    } else {
      \/.left(s"$url (invalid zip file - folder '$target' not created")
    }
    
  } catch {
    case ex: FileNotFoundException => \/.left(ex.getMessage)
  }

  def checkContent(path: File) : \/[String, String]= {
    
    val filter = new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = {
        name == "index.html"
      }
    }
    
    def nullIsEmpty[T](arr: Array[T]) : List[T] = arr match {
      case null => List[T]()
      case arr => arr.toList
    }
    
    def mainPath(path: File) : Option[String] = nullIsEmpty(path.list(filter)).headOption
    def subPath(path: File) : Option[String] =  nullIsEmpty(path.listFiles()).flatMap(x => nullIsEmpty(x.list(filter)).headOption.map(x + File.separator + _)).headOption
    val index: Option[String] = mainPath(path) orElse subPath(path)
    index.map(f => \/.right(f.replace("index.html", ""))) getOrElse \/.left(s"index.html file not found ($path)")
  }

}

object WebsiteDb extends DbImpl[Website] {

  def config = Play.application().configuration().getString("db.website")

  def createDefaultList: List[Website] = {
    implicit val websiteFormat = Json.format[Website]
    Website(0, "admin", "", 10000, "admin") :: Json.fromJson[List[Website]](Json.parse(config)).fold(
      _ => List(),
      list => list
    )
  }

}

