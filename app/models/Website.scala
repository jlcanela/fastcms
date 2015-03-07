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

import java.io._

case class Website(id: Int, name: String, url: String, port: Int, path: String, proxy: Boolean = false) {

  def log(rootLogPath: File) = new File(rootLogPath, s"$name")
  def www(wwwPath: File) = path match {
    case "" => new File(wwwPath, name)
    case p => new File(p)
  } 
  

}



