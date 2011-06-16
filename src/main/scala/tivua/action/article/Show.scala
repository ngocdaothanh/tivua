package tivua.action.article

import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import xitrum.action.annotation.GET

import tivua.action.Application
import tivua.model.Article

@GET("/articles/:id")
class Show extends Application {
  override def execute {
    val id = param("id")
    Article.first(id) match {
      case None =>
        response.setStatus(NOT_FOUND)
        flash("Not found")
        renderView("")

      case Some(article) =>
        at("title") = article.title
        renderView(
          <div class="article">
            <h2>{article.title}</h2>
            <div>{article.teaser}</div>
            <div>{article.body}</div>
          </div>)
    }
  }
}
