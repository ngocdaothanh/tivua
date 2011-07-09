package tivua.model

import java.util.Date
import scala.collection.mutable.ArrayBuffer

import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId

class Article(
    var id:              String,
    var title:           String,
    var teaser:          String,
    var body:            String,
    var userId:          String,
    var sticky:          Boolean,
    var hits:            Int,
    var createdAt:       Date,
    var updatedAt:       Date,
    var threadUpdatedAt: Date,
    var categories:      Iterable[Category]) {

  def this() = this(null, null, "", "", "", false, 1, null, null, null, null)
}

object ArticleColl {
  val COLL              = "articles"

  val ID                = "_id"
  val TITLE             = "title"
  val TEASER            = "teaser"
  val BODY              = "body"
  val USER_ID           = "user_id"
  val STICKY            = "sticky"
  val HITS              = "hits"
  val CREATED_AT        = "created_at"
  val UPDATED_AT        = "updated_at"
  val THREAD_UPDATED_AT = "thread_updated_at"
}

object ArticleCategoryColl {
  val COLL              = "articles_categories"

  val ID                = "_id"
  val ARTICLE_ID        = "article_id"
  val CATEGORY_ID       = "category_id"
  val THREAD_UPDATED_AT = "thread_updated_at"
}

object Article {
  private val ITEMS_PER_PAGE = 10

  private val articleColl         = DB.db(ArticleColl.COLL)
  private val articleCategoryColl = DB.db(ArticleCategoryColl.COLL)

  //----------------------------------------------------------------------------

  def page(p: Int): (Int, Iterable[Article]) = {
    val count = articleColl.count.toInt
    val numPages = (count / ITEMS_PER_PAGE) + (if (count % ITEMS_PER_PAGE == 0) 0 else 1)

    val cur = articleColl.find().sort(MongoDBObject("sticky" -> -1, "thread_updated_at" -> -1)).skip((p - 1) * ITEMS_PER_PAGE).limit(ITEMS_PER_PAGE)
    val buffer = ArrayBuffer[Article]()
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

  def first(id: String): Option[Article] = articleColl.findOneByID(new ObjectId(id)).map(mongoToScala)

  def categoryPage(categoryId: String, page: Int): (Int, Iterable[Article]) = {
    val count = articleCategoryColl.count(MongoDBObject("category_id" -> categoryId)).toInt
    val numPages = (count / ITEMS_PER_PAGE) + (if (count % ITEMS_PER_PAGE == 0) 0 else 1)

    val cur = articleCategoryColl.find(MongoDBObject("category_id" -> categoryId)).sort(MongoDBObject("sticky" -> -1, "thread_updated_at" -> -1)).skip((page - 1) * ITEMS_PER_PAGE).limit(ITEMS_PER_PAGE)
    val buffer = ArrayBuffer[Article]()
    for (o <- cur) {
      val articleId = o.as[String](ArticleCategoryColl.ARTICLE_ID)
      first(articleId).foreach(buffer.append(_))
    }

    (numPages, buffer)
  }

  //----------------------------------------------------------------------------

  private def mongoToScala(mongo: DBObject) = {
    val id        = mongo._id.get.toString
    val title     = mongo.as[String] (ArticleColl.TITLE)
    val teaser    = mongo.as[String] (ArticleColl.TEASER)
    val body      = mongo.as[String] (ArticleColl.BODY)
    val userId    = mongo.as[String] (ArticleColl.USER_ID)
    val sticky    = mongo.as[Boolean](ArticleColl.STICKY)
    val hits      = mongo.as[Int]    (ArticleColl.HITS)
    val createdAt = mongo.as[Date]   (ArticleColl.CREATED_AT)
    val updatedAt = mongo.as[Date]   (ArticleColl.UPDATED_AT)
    val threadUpdatedAt = mongo.as[Date]   (ArticleColl.THREAD_UPDATED_AT)

    val categories = {
      val cur = articleCategoryColl.find(MongoDBObject("article_id" -> id))
      val buffer = ArrayBuffer[Category]()
      for (o <- cur) {
        val categoryId = o.as[String](ArticleCategoryColl.CATEGORY_ID)
        Category.first(categoryId).foreach(buffer.append(_))
      }
      buffer.sortBy(_.position)
    }

    new Article(id, title, teaser, body, userId, sticky, hits, createdAt, updatedAt, threadUpdatedAt, categories)
  }
}
