package tivua.action.article

import scala.xml.Unparsed
import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import xitrum.annotation.GET

import tivua.action.AppAction
import tivua.helper.ArticleHelper
import tivua.model.{Article, Comment}

@GET("/articles/:id/:titleInUrl")
class Show extends AppAction with ArticleHelper {
  override def execute {
    val id = param("id")
    Article.first(id) match {
      case None =>
        response.setStatus(NOT_FOUND)
        flash("Not found")
        at("title") = "Not found"
        renderView("")

      case Some(article) =>
        val comments = Comment.all(article.id)

        at("title") = article.title
        renderView(
          <div>
            <div class="article">
              <h1>{article.title}</h1>
              {renderArticleMetaData(article)}
              <div>{Unparsed(article.teaser)}</div>
              <div>{Unparsed(article.body)}</div>
            </div>

            {if (!comments.isEmpty)
              <hr />
              <h2>Comments</h2>
              <ul class="comments">
                {comments.map {c => <li>{renderComment(c)}</li> }}
              </ul>
            }
          </div>)
    }
  }
}
