import api.WebsiteApi
import play.api._


object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    WebsiteApi.start
    Logger.info(s"nginx started on port ${WebsiteApi.adminPort}")
  }
  
  override def onStop(app: Application): Unit = {
    WebsiteApi.stop
  }

}