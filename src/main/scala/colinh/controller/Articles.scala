package colinh.controller

import org.jboss.netty.handler.codec.http.HttpResponseStatus
import colinh.model.Article

class Articles extends Application {
  def index {
    val page = paramo("page").getOrElse("1").toInt
    val (numPages, articles) = Article.page(page)
    at("page",     page)
    at("numPages", numPages)
    at("articles", articles)
    render
  }

  def show {
    val id = param("id").toLong
    Article.first(id) match {
      case None =>
        response.setStatus(HttpResponseStatus.NOT_FOUND)
        render("Errors#error404")

      case Some(article) =>
        at("article", article)
        render
    }
  }

  def make {
    val article = new Article
    at("article", article)
    render
  }

  def create {
    val article = new Article
    article.title  = param("title")
    article.teaser = param("teaser")
    article.body   = param("body")

    at("article", article)
    render("make")
  }

  def edit {

  }
}
