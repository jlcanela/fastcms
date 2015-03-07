package test

import models.RoutingRule


trait RuleFixture {

  val defaultRules =
    RoutingRule(0, "", "/index.html", "index", 0) ::
    RoutingRule(1, "", "/([a-z-_]*).html", "{0}", 1) ::
    RoutingRule(2, "", "/([a-z-_]*)/([a-z-_]*).html", "{0}{1}", 1) ::
    Nil
  

}
