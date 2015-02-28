package test

import models.Rule


trait RuleFixture {

  val defaultRules =
    Rule(0, "", "/index.html", "index", 0) ::
    Rule(1, "", "/([a-z-_]*).html", "{0}", 1) ::
    Rule(2, "", "/([a-z-_]*)/([a-z-_]*).html", "{0}{1}", 1) ::
    Nil
  

}
