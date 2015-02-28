package models


case class Source(name: String, url: String)

object Source {

  import java.sql.Connection
  import anorm._

  def fetch(implicit conn: Connection): List[Source] = {
    import anorm.SqlParser._
    val parser = str("NAME") ~ str("URL") *
    val query = SQL("Select * from SOURCE")
    val convert: ~[String, String] => Source = { case (name: String) ~ (url: String) => Source(name, url) }
    query.as(parser) map convert
  }


}