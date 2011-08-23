package tivua.action

import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import xitrum.annotation.GETs

import tivua.helper.ArticleHelper
import tivua.model.Article

@GETs(Array("/categories/:id/:nameInUrl", "/categories/:id/:nameInUrl/:page"))
class CategoryShow extends AppAction with ArticleHelper {
  override def execute {
    val id        = param("id")
    val nameInUrl = param("nameInUrl")
    val page      = paramo("page").getOrElse("1").toInt

    RVar.categories.get.find(_.id == id) match {
      case None =>
        response.setStatus(NOT_FOUND)
        val title = "Category not found"
        RVar.title.set(title)
        renderView(title)

      case Some(category) =>
        val title = "Category: %s".format(category.name)
        RVar.title.set(title)
        RVar.category.set(category)

        val (numPages, articles) = Article.categoryPage(category.id, page)
        val links = renderPaginationLinks(numPages, page, "/categories/" + id + "/" + nameInUrl + "/%s")
        renderView(
          <xml:group>
            <b>{title}</b>
            <hr />

            {links}
            <ul class="articles">
              {articles.map { a => <li>{renderArticlePreview(a)}</li> }}
            </ul>
            {links}
          </xml:group>
        )
    }
  }
}

