package tivua.action.article

import scala.collection.mutable.ArrayBuffer
import scala.xml.Unparsed

import xitrum.annotation.GETs

import tivua.action.AppAction
import tivua.helper.ArticleHelper
import tivua.model.{Article, Comment}

@GETs(Array("/", "/articles/page/:page"))
class Index extends AppAction with ArticleHelper {
  override def execute {
    val page = paramo("page").getOrElse("1").toInt
    val (numPages, articles) = Article.page(page)

    at("title") = "Home"
    val links   = renderPaginationLinks(numPages, page, "/articles/page/%s")
    renderView(ArrayBuffer(
      links,
      <ul class="articles">
        {articles.map { a => <li>{renderArticlePreview(a)}</li> }}
      </ul>,
      links
    ))
  }

  private def renderArticlePreview(article: Article) = {
    val commento = Comment.lastComment(article.id)
    val url = urlFor[Show]("id" -> article.id, "titleInUrl" -> titleInUrl(article.title))

    <div>
      <h1><a href={url}>{article.title}</a></h1>
      {renderArticleMetaData(article)}
      <div>{Unparsed(article.teaser)}</div>

      <p><a href={url}>→ Read more</a></p>

      {if (commento.isDefined) {
        <div>{renderComment(commento.get)}</div>
      }}
    </div>
  }
}
