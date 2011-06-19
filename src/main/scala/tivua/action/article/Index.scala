package tivua.action.article

import scala.collection.mutable.ArrayBuffer
import scala.xml.Unparsed

import xitrum.annotation.GETs

import tivua.action.AppAction
import tivua.model.Article

@GETs(Array("/", "/articles/page/:page"))
class Index extends AppAction {
  override def execute {
    val page = paramo("page").getOrElse("1").toInt
    val (numPages, articles) = Article.page(page)

    at("title") = "Home"
    val links   = renderPaginationLinks(numPages, page, "/articles/page/%s")
    renderView(ArrayBuffer(
      links,
      <ul class="articles">
        {for (a <- articles) yield
          <li>
            <h1><a href={"/articles/" + a.id + "/" + titleInUrl(a.title)}>{a.title}</a></h1>
            <div>
              {Unparsed(a.teaser)}
            </div>
          </li>
        }
      </ul>,
      links
    ))
  }
}
