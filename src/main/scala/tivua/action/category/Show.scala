package tivua.action.category

import xitrum.annotation.GETs

import tivua.action.AppAction
import tivua.helper.ArticleHelper
import tivua.model.Category

@GETs(Array("/categories/:id/:nameInUrl", "/categories/:id/:nameInUrl/:page"))
class Show extends AppAction with ArticleHelper {
  override def execute {
    val id        = param("id")
    val nameInUrl = param("nameInUrl")
    val page      = paramo("page").getOrElse("1").toInt

    Category.articlesPage(id, page) match {
      case None =>
        at("title") = "Category"
        renderView("Category not found")

      case Some((category, (numPages, articles))) =>
        at("title") = "Category %s".format(category.name)
        val links   = renderPaginationLinks(numPages, page, "/categories/" + id + "/" + nameInUrl + "/%s")
        renderView(
          <div>
            <b>{"Category: %s".format(category.name)}</b>
            <hr />

            {links}
            <ul class="articles">
              {articles.map { a => <li>{renderArticlePreview(a)}</li> }}
            </ul>
            {links}
          </div>
        )
    }
  }
}

