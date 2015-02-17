package models

import scalaz.\/

case class Webserver() {
  def generateConfig(websites: List[Website]): \/[String, Unit]= {
    \/.left("not implemented")
  }
}
