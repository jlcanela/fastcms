package api

import models.RoutingRule

object RuleApi {

  import java.sql.Connection
  import anorm._

  def fetchRoutingRule(implicit conn: Connection): List[RoutingRule] = {
    import anorm.SqlParser._

    SQL("Select * from RULE").as(int("ID") ~ str("HOST") ~ str("URI") ~ str("CONTENT") ~ int("PRIORITY") *) map {
      case id ~ host ~ uri ~ content ~ priority => RoutingRule(id, host, uri, content, priority)
    }

  }

}
