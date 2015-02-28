package models

//case class Source(name: String, url: String)

case class Page(uri: String, sources: List[Source], template: String) {
}

case class Template(path: String)

/*
import scala.xml.{Text, NodeSeq}

trait Source[T] {
  def apply(): List[T]
}


case class Article(author: String, title: String)


trait Request

case class HttpRequest(uri: String, profile: String, device: String)

trait Content

case class Page(val html: NodeSeq) extends Content

case class Blog() extends Content

case class Layout()

case class Route()

case class Router() {
  def apply() = Blog()
}

object Sample {

  val articles = Article("John", "Il était une fois ...") :: Article("Marie", "L'escalade") :: Nil
  val articleSource = new Source[Article] { def apply = articles }
  
  val authorHtml =  
    <table>
      <th><td>Author</td><td>Title</td></th>
      <tr class="authors"><td class="author">Author</td><td class="title">Title</td></tr>
    </table>
  
  def matcher(contents: List[Content]) : Request => Content = ???

  val homePage = new Page(authorHtml)
  
/*  type Transformer[T] = T => NodeSeq
  val authorT: Transformer[Article] = article => Text(article.author)
*/

import org.fusesource.scalate.scuery.Transform._
  object transformer extends Transformer {
    $(".person") { node =>
      people.flatMap { p =>
        new Transform(node) {
          $(".name").contents = p.name
          $(".location").contents = p.location
        }
      }
    }
  }
  
  {
   
//    val articles = ".author" -> authorT
 //   val nameTransform = ".article"
    
  }
  def simplePage = {
    
  
    
    
    
  }
  
}*/