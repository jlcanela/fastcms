import api.WebsiteApi
import play.Play
import play.api._


object Global extends GlobalSettings {


  override def onStart(app: Application): Unit = {
    if (app.configuration.getBoolean("nginx.autostartstop").getOrElse( false)) {
      WebsiteApi.start
      Logger.info(s"nginx started on port ${WebsiteApi.adminPort}")
    } else {
      Logger.info("not starting nginx as nginx.autostartstop is deactivated")
    }
  }
  
  override def onStop(app: Application): Unit = {
    if (app.configuration.getBoolean("nginx.autostartstop").getOrElse( false)) {
      WebsiteApi.stop
    } else {
      Logger.info("not stopping nginx as nginx.autostartstop is deactivated")
    }
  }

}