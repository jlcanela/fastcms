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

import play.Play
import play.api.libs.json.Json
import play.api.{Logger, Configuration}


case class Website(id: Int, name: String,url: String)

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