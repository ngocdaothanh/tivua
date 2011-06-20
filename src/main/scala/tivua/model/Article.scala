package tivua.model

import java.util.Date
import scala.collection.mutable.ArrayBuffer

import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId

class Article(
    var id:        String,
    var userId:    String,
    var title:     String,
    var teaser:    String,
    var body:      String,
    var sticky:    Boolean,
    var hits:      Int,
    var createdAt: Date,
    var updatedAt: Date) {

  def this() = this(null, null, "", "", "", false, 1, null, null)
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
  val USER_ID    = "user_id"
}

object Article {
  import ArticleColl._

  val ITEMS_PER_PAGE = 10

  val coll = DB.db(COLL)

  //----------------------------------------------------------------------------

  def page(p: Int): (Int, Iterable[Article]) = {
    val count = coll.count.toInt
    val numPages = (count / ITEMS_PER_PAGE) + (if (count % ITEMS_PER_PAGE == 0) 0 else 1)

    val cur = coll.find().sort(MongoDBObject("updated_at" -> -1)).skip((p - 1) * ITEMS_PER_PAGE).limit(ITEMS_PER_PAGE)
    val buffer = new ArrayBuffer[Article]
    for (o <- cur) {
      val a = mongoToScala(o)
      buffer.append(a)
    }

    (numPages, buffer)
  }

  def page(p: Int, ids: Array[String]): (Int, Iterable[Article]) = {
    val count = ids.length
    val numPages = (count / ITEMS_PER_PAGE) + (if (count % ITEMS_PER_PAGE == 0) 0 else 1)

    val min = (p - 1) * ITEMS_PER_PAGE
    val max1 = p * ITEMS_PER_PAGE
    val max2 = if (max1 > count - 1) count - 1 else max1
    val articles = (min to max2).flatMap { id => first(ids(id)) }

    (numPages, articles)
  }

  def first(id: String): Option[Article] = coll.findOneByID(new ObjectId(id)).map(mongoToScala)

  //----------------------------------------------------------------------------

  private def mongoToScala(mongo: DBObject) = {
    val id        = mongo._id.get.toString
    val title     = mongo.as[String] (TITLE)
    val teaser    = mongo.as[String] (TEASER)
    val body      = mongo.as[String] (BODY)
    val sticky    = mongo.as[Boolean](STICKY)
    val hits      = mongo.as[Int]    (HITS)
    val createdAt = mongo.as[Date]   (CREATED_AT)
    val updatedAt = mongo.as[Date]   (UPDATED_AT)
    val userId    = mongo.as[String] (USER_ID)

    new Article(id, userId, title, teaser, body, sticky, hits, createdAt, updatedAt)
  }
}
