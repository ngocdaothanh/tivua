package tivua.action

import tivua.model.Article

class ArticlesShow extends Application {
  def execute {
    val id = param("id").toLong
    Article.first(id) match {
      case None =>
        response.setStatus(NOT_FOUND)
        render("Errors#error404")

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
