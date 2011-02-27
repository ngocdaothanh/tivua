package tivua.model

import scala.collection.mutable.ArrayBuffer
import com.mongodb.casbah.Imports._

class Article(
    var id:        String,
    var title:     String,
    var teaser:    String,
    var body:      String,
    var sticky:    Boolean,
    var hits:      Int,
    var createdAt: Int,
    var updatedAt: Int,
    var userId:    String) {
  def this() = this("", "", "", "", false, 0, 0, 0, "")
}

object ArticleColl {
  val COLL       = "articles"

  val ID         = "_id"
  val TITLE      = "title"
  val TEASER     = "teaser"
  val BODY       = "body"
  val STICKY     = "sticky"
  val HITS       = "hits"
  val CREATED_AT = "created_at"
  val UPDATED_AT = "updated_at"
  val USER_ID    = "uid"
}

object Article {
  import ArticleColl._

  val ITEMS_PER_PAGE = 10

  val coll = DB.db(COLL)

  //----------------------------------------------------------------------------

  def all: Iterable[Article] = {
    var cur = coll.find
    val buffer = new ArrayBuffer[Article]
    for (o <- cur) {
      val a = mongoToScala(o)
      buffer.append(a)
    }
    buffer
  }

  def page(p: Int): (Int, Iterable[Article]) = {
    val (numPages, coll) = {
      (1, new ArrayBuffer[Article])
    }
    (numPages, coll)
  }

  def first(id: String): Option[Article] = coll.findOneByID(id).map(mongoToScala)

  //----------------------------------------------------------------------------

  def mongoToScala(mongo: DBObject): Article = {
    val id        = mongo._id.get.toString
    val title     = mongo.as[String] (TITLE)
    val teaser    = mongo.as[String] (TEASER)
    val body      = mongo.as[String] (BODY)
    val sticky    = mongo.as[Boolean](STICKY)
    val hits      = mongo.as[Int]    (HITS)
    val createdAt = mongo.as[Int]    (CREATED_AT)
    val updatedAt = mongo.as[Int]    (UPDATED_AT)
    val userId    = mongo.as[String] (USER_ID)

    new Article(id, title, teaser, body, sticky, hits, createdAt, updatedAt, userId)
  }
}
