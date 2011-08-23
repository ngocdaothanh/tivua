package tivua.action

import scala.xml.Unparsed
import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import xitrum.annotation.GET

import tivua.helper.ArticleHelper
import tivua.model.{Article, Comment}

@GET("/articles/:id/:titleInUrl")
class ArticleShow extends AppAction with ArticleHelper {
  override def execute {
    val id = param("id")
    Article.first(id) match {
      case None =>
        response.setStatus(NOT_FOUND)
        val title = "Article not found"
        RVar.title.set(title)
        renderView(title)

      case Some(article) =>
        val comments = Comment.all(article.id)

        RVar.title.set(article.title)
        renderView(
          <xml:group>
            <div class="article">
              <h1>{article.title} {if (article.sticky) <img src={urlForPublic("img/sticky.png")} />}</h1>
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
          </xml:group>)
    }
  }
}
