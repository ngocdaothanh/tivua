package tivua.action

import xitrum.annotation.GETs

import tivua.helper.ArticleHelper
import tivua.model.Article

@GETs(Array("/", "/articles/page/:page"))
class ArticleIndex extends AppAction with ArticleHelper {
  override def execute {
    val page = paramo("page").getOrElse("1").toInt
    val (numPages, articles) = Article.page(page)

    Var.rTitle.set("Home")
    val links = renderPaginationLinks(numPages, page, "/articles/page/%s")
    renderView(
      <xml:group>
        {links}
        <ul class="articles">
          {articles.map { a => <li>{renderArticlePreview(a)}</li> }}
        </ul>
       {links}
      </xml:group>)
  }
}
