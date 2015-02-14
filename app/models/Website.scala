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

import java.io.{FileNotFoundException, FileInputStream, File}
import java.net.URL

import org.zeroturnaround.zip.ZipUtil
import play.Play
import play.api.libs.json.Json
import play.api.{Logger, Configuration}


import scalaz.syntax.validation._
import scalaz.Validation

case class Website(id: Int, name: String,url: String) {

  /**
   * fetch the file and unzip it to path
   * @param path
   */
  def fetchContent(path: String) : Validation[String, Unit]= try {
    val target = new File(path)
    val asUrl = new URL(url)
    val stream = asUrl.getProtocol match {
      case "file" => new FileInputStream(asUrl.getFile)
      case _ => new URL(url).openStream()
    }

    ZipUtil.unpack(stream, target)
    if (target.exists()) {
      ().success[String]
    } else {
     s"$url (invalid zip file - folder '$path' not created".failure[Unit]
    }
    
  } catch {
    case ex: FileNotFoundException => ex.getMessage.failure[Unit]
  }

  def checkContent(path: String) : Validation[String, Unit]= {
    val indexPath = new File(new File(path), "index.html")
    if (indexPath.exists()) {
      ().success[String]
    } else {
      s"index.html file not found ($indexPath)".failure[Unit]
    }
       
  }

}

object WebsiteDb extends DbImpl[Website] {

  def config = Play.application().configuration().getString("db.website")

  def createDefaultList: List[Website] = {
    implicit val websiteFormat = Json.format[Website]
    Json.fromJson[List[Website]](Json.parse(config)).fold(
      _ => List(),
      list => list
    )
  }

}