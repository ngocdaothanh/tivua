package tivua.helper

import java.text.SimpleDateFormat
import scala.xml.Unparsed

import tivua.model.{Article, Comment}

trait ArticleHelper {
  private val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm")

  def renderArticleMetaData(article: Article) = {
    <div class="article_metadata">
      <div class="grid_1"><fb:profile-pic uid={article.userId} facebook-logo="true" /></div>
      <div style="margin-left: 1em" class="prefix_1">
        <b><fb:name uid={article.userId}></fb:name></b>
        Hits: {article.hits} |
        Created: {dateFormat.format(article.createdAt)}
        {if (article.updatedAt != article.createdAt) "| Updated: " + dateFormat.format(article.updatedAt) }
      </div>
      <div class="clear"></div>
    </div>
  }

  def renderComment(comment: Comment) = {
    <div class="comment">
      <div class="grid_1"><fb:profile-pic uid={comment.userId} facebook-logo="true" /></div>
      <div style="margin-left: 1em" class="prefix_1">
        <div class="comment_metadata">
          <b><fb:name uid={comment.userId}></fb:name></b>
          Created: {dateFormat.format(comment.createdAt)}
          {if (comment.updatedAt != comment.createdAt) "| Updated: " + dateFormat.format(comment.updatedAt) }
        </div>
        {Unparsed(comment.body)}
      </div>
      <div class="clear"></div>
    </div>
  }
}
