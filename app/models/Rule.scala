package models

import java.text.MessageFormat

case class Rule(id: Int, host: String, uri: String, content: String, priority: Int) {
  
  def findContent(host: String, uri: String) = {
    this.uri.r.unapplySeq(uri) match {
      case Some(x) => Some(MessageFormat.format(content, x.map(_.toString) :_*))
      case _ => None
    }
  }
}

object Rule {

  import java.sql.Connection
  import anorm._

  def fetch(implicit conn: Connection): List[Rule] = {
    import anorm.SqlParser._

    SQL("Select * from RULE").as(int("ID") ~ str("HOST") ~ str("URI") ~ str("CONTENT") ~ int("PRIORITY") *) map {
      case id ~ host ~ uri ~ content ~ priority => Rule(id, host, uri, content, priority) 
    }

  }

}
