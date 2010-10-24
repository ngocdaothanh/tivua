package colinh.controller

import org.jboss.netty.handler.codec.http.HttpMethod._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

import colinh.model.Article

object Articles {
	val routes = List(
    (GET,  "/",                    "Articles#index"),
    (GET,  "/articles/page/:page", "Articles#index"),
    (GET,  "/articles/make",       "Articles#make"),
    (POST, "/articles",            "Articles#create"),
    (GET,  "/articles/:id",        "Articles#show"),
    (GET,  "/articles/:id/edit",   "Articles#edit"))
}

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
        response.setStatus(NOT_FOUND)
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
