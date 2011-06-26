package tivua.helper

import java.text.SimpleDateFormat
import scala.xml.Unparsed

import tivua.action.{ArticleShow, CategoryShow}
import tivua.model.{Article, Comment}

trait ArticleHelper extends AppHelper {
  private val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm")

  def renderArticlePreview(article: Article) = {
    val commento = Comment.lastComment(article.id)
    val url = urlFor[ArticleShow]("id" -> article.id, "titleInUrl" -> titleInUrl(article.title))

    <xml:group>
      <h1><a href={url}>{article.title}</a> {if (article.sticky) <img src={urlForPublic("img/sticky.png")} />}</h1>
      {renderArticleMetaData(article, false)}
      <div>{Unparsed(article.teaser)}</div>

      <p><a href={url}>→ Read more</a></p>

      {if (commento.isDefined) {
        <div>{renderComment(commento.get)}</div>
      }}
    </xml:group>
  }

  def renderArticleMetaData(article: Article, showLike: Boolean = true) = {
    val categories = article.categories.filter(!_.toBeCategorized).map { c =>
      <a href={urlFor[CategoryShow]("id" -> c.id, "nameInUrl" -> titleInUrl(c.name))}>{c.name}</a>
    }

    <table class="article_metadata">
      <tr>
        <td><fb:profile-pic uid={article.userId} facebook-logo="true" /></td>
        <td style="vertical-align: top; padding-left: 1em">
          <b><fb:name uid={article.userId} useyou="false"></fb:name></b><br />

          Hits: {article.hits} |
          Created: {dateFormat.format(article.createdAt)}
          {if (article.updatedAt != article.createdAt) "| Updated: " + dateFormat.format(article.updatedAt) }<br />

          {if (!categories.isEmpty)
            <xml:group>
              Category: {Unparsed(categories.mkString(", "))}<br />
            </xml:group>
          }

          {if (showLike) <fb:like send="true" layout="button_count"></fb:like>}
        </td>
      </tr>
    </table>
  }

  def renderComment(comment: Comment) = {
    <table class="comment">
      <tr>
        <td><fb:profile-pic uid={comment.userId} facebook-logo="true" /></td>
        <td style="vertical-align: top; padding-left: 1em; width: 100%">
          <div class="comment_metadata">
            <b><fb:name uid={comment.userId} useyou="false"></fb:name></b>
            Created: {dateFormat.format(comment.createdAt)}
            {if (comment.updatedAt != comment.createdAt) "| Updated: " + dateFormat.format(comment.updatedAt) }
          </div>
          <div>
          {Unparsed(comment.body)}
          </div>
        </td>
      </tr>
    </table>
  }
}
