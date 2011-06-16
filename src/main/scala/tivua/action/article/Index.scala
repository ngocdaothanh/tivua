package tivua.action.article

import scala.collection.mutable.ArrayBuffer
import xitrum.action.annotation.GET

import tivua.action.Application
import tivua.model.Article

@GET("/")
class Index extends Application {
  override def execute {
    val page = paramo("page").getOrElse("1").toInt
    val (numPages, articles) = Article.page(page)

    at("title") = "Home"
    val links   = renderPaginationLinks(numPages, page, "/articles/page/")
    renderView(ArrayBuffer(
      links,
      <ul class="articles">
        {for (a <- articles) yield
          <li>
            <h2><a href={"/articles/" + a.id}>{a.title}</a></h2>
            <div>
              {a.teaser}
            </div>
          </li>
        }
      </ul>,
      links
    ))
  }
}
