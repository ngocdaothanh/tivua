package tivua.action.article

import xitrum.annotation.GETs

import tivua.action.AppAction
import tivua.helper.ArticleHelper
import tivua.model.Article

@GETs(Array("/", "/articles/page/:page"))
class Index extends AppAction with ArticleHelper {
  override def execute {
    val page = paramo("page").getOrElse("1").toInt
    val (numPages, articles) = Article.page(page)

    at("title") = "Home"
    val links   = renderPaginationLinks(numPages, page, "/articles/page/%s")
    renderView(
      <div>
        {links}
        <ul class="articles">
          {articles.map { a => <li>{renderArticlePreview(a)}</li> }}
        </ul>
       {links}
      </div>)
  }
}
