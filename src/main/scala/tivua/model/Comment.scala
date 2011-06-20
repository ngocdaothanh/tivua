package tivua.model

import java.util.Date
import scala.collection.mutable.ArrayBuffer

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

class Comment(
    var id:        String,
    var articleId: String,
    var userId:    String,
    var body:      String,
    var createdAt: Date,
    var updatedAt: Date)

object CommentColl {
  val COLL       = "comments"

  val ID         = "_id"
  val ARTICLE_ID = "article_id"
  val USER_ID    = "user_id"
  val BODY       = "body"
  val CREATED_AT = "created_at"
  val UPDATED_AT = "updated_at"
}

object Comment {
  import CommentColl._

  val ITEMS_PER_PAGE = 10

  val coll = DB.db(COLL)

  //----------------------------------------------------------------------------

  def all(articleId: String): Iterable[Comment] = {
    val cond = MongoDBObject("article_id" -> articleId)
    val cur = coll.find(cond).sort(MongoDBObject("created_at" -> 1))
    val buffer = new ArrayBuffer[Comment]
    for (o <- cur) {
      val c = mongoToScala(o)
      buffer.append(c)
    }
    buffer
  }

  def lastComment(articleId: String): Option[Comment] = {
    val cond = MongoDBObject("article_id" -> articleId)
    val cur = coll.find(cond).sort(MongoDBObject("created_at" -> -1)).limit(1)
    val buffer = new ArrayBuffer[Comment]
    for (o <- cur) {
      val c = mongoToScala(o)
      buffer.append(c)
    }
    if (buffer.isEmpty) None else Some(buffer.first)
  }

  //----------------------------------------------------------------------------

  private def mongoToScala(mongo: DBObject) = {
    val id        = mongo._id.get.toString
    val articleId = mongo.as[String] (ARTICLE_ID)
    val userId    = mongo.as[String] (USER_ID)
    val body      = mongo.as[String] (BODY)
    val createdAt = mongo.as[Date]   (CREATED_AT)
    val updatedAt = mongo.as[Date]   (UPDATED_AT)

    new Comment(id, articleId, userId, body, createdAt, updatedAt)
  }
}
