package tivua.action.article

import scala.xml.Unparsed
import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import xitrum.annotation.GET

import tivua.action.AppAction
import tivua.model.Article

@GET("/articles/:id")
class Show extends AppAction {
  override def execute {
    val id = param("id")
    Article.first(id) match {
      case None =>
        response.setStatus(NOT_FOUND)
        flash("Not found")
        at("title") = "Not found"
        renderView("")

      case Some(article) =>
        at("title") = article.title
        renderView(
          <div class="article">
            <h1>{article.title}</h1>
            <div>{Unparsed(article.teaser)}</div>
            <div>{Unparsed(article.body)}</div>
          </div>)
    }
  }
}
