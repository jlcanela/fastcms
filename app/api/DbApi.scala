package api

import models.{AggregationRule, Article, Source, RoutingRule}
import anorm.SqlParser._

object DbApi {

  import java.sql.Connection
  import anorm._

  def fetchRoutingRule(implicit conn: Connection): List[RoutingRule] = {

    SQL("Select * from RULE").as(int("ID") ~ str("HOST") ~ str("URI") ~ str("CONTENT") ~ int("PRIORITY") *) map {
      case id ~ host ~ uri ~ content ~ priority => RoutingRule(id, host, uri, content, priority)
    }

  }

  def fetchAggregationRule(implicit conn: Connection): List[AggregationRule] = {

    SQL("Select * from AGGREGATIONRULE").as(int("ID") ~ str("CONTENT") ~ str("SOURCE") ~ str("TARGET") *) map {
      case id ~ content ~ source ~ target  => AggregationRule(content, source, target)
    }

  }

  def fetchSource(implicit conn: Connection): List[Source] = {

    SQL("Select * from SOURCE").as(str("NAME") ~ str("URL") *) map {
      case (name: String) ~ (url: String) => Source(name, url)
    }

  }

  def fetchArticle(implicit conn: java.sql.Connection): List[Article] = {

    SQL("Select * from ARTICLE").as(str("AUTHOR") ~ str("CONTENT") *) map {
      case (author: String) ~ (content: String) => Article(author, content)
    }

  }


}
