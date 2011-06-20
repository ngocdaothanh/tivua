package tivua.action.category

import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import xitrum.annotation.GETs

import tivua.action.{AppAction, Var}
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
        response.setStatus(NOT_FOUND)
        val title = "Category not found"
        Var.rTitle.set(title)
        renderView(title)

      case Some((category, (numPages, articles))) =>
        val title = "Category: %s".format(category.name)
        Var.rTitle.set(title)
        Var.rCategory.set(category)
        val links = renderPaginationLinks(numPages, page, "/categories/" + id + "/" + nameInUrl + "/%s")
        renderView(
          <div>
            <b>{title}</b>
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

