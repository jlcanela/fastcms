package models

import java.io.{File, FileInputStream, FileNotFoundException, FilenameFilter, FileFilter}
import java.net.URL

import org.zeroturnaround.zip.ZipUtil

import play.api.libs.json.Json
import play.api.{Logger, Configuration}


import scalaz.\/

case class WebsiteDb(configDb: String, adminPort: Int, wwwPath: File) extends DbImpl[Website] {

  def this(config: play.Configuration) = this(config.getString("jsondb.website"), config.getInt("admin.port"), new java.io.File(config.getString("nginx.www")))
  //def config = Play.application().configuration()
  /*def configDb = config.getString("db.website")
  def adminPort = config.getInt("admin.port")
  def wwwPath = new File(config.getString("nginx.www"))
*/

  /**
   * fetch the file and unzip it to path
   * @param path
   */
  def fetchContent(url: String, target: File) : \/[String, Unit]= try {
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

  def findWebsites : List[(String, String)] = try {
    val directoryFilter = new FileFilter {
      override def accept(pathname: File): Boolean = pathname.isDirectory
    }
    Option(wwwPath) map { wwwPath =>
      wwwPath.listFiles(directoryFilter).toList.flatMap { p =>
        checkContent(p) fold (
          err => None,
          _ match {
            case "" => Some((p.getName, ""))
            case path =>  Some((p.getName, path))
          }
        )
      }
    } getOrElse {
      Logger.logger.warn("wwwPath not initialized :" + wwwPath)
      List()
    }
  } catch {
    case ex: Exception => Logger.logger.error(ex.getClass.toString + " - " + ex.getMessage)
      ex.printStackTrace()
      List()
  }

  def createDefaultList: List[Website] = {

    val admin = new Website(0, "admin", "", adminPort, "admin") {
      override def www(path: File) = new File("admin")
    }

    val websites = if (Option(configDb).map(_.isEmpty) getOrElse(true)) {
      (findWebsites zipWithIndex) map { case ((name, path), index) => Website(index + 1, name, "", adminPort + index + 1, path, true)}
    } else {
      implicit val websiteFormat = Json.format[Website]
      Json.fromJson[List[Website]](Json.parse(configDb)).fold(
        _ => List(),
        list => list
      )
    }

    admin :: websites

  }

}
