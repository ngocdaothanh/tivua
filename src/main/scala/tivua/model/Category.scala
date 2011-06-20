package tivua.model

import java.util.Date
import scala.collection.mutable.ArrayBuffer

import com.mongodb.BasicDBList
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

class Category(
    var id:         String,
    var name:       String,
    var position:   Int,
    var articleIds: Array[String])

object CategoryColl {
  val COLL       = "categories"

  val ID          = "_id"
  val NAME        = "name"
  val POSITION    = "position"
  val ARTICLE_IDS = "article_ids"
}

object Category {
  import CategoryColl._

  val coll = DB.db(COLL)

  //----------------------------------------------------------------------------

  def all: Iterable[Category] = {
    val cur = coll.find().sort(MongoDBObject("position" -> 1))
    val buffer = new ArrayBuffer[Category]
    for (o <- cur) {
      val c = mongoToScala(o)
      buffer.append(c)
    }
    buffer
  }
/*
  def articles(id: String): Iterable[Article] = {
    val category = coll.findOneByID(new ObjectId(id)).map(mongoToScala)
    //category.articleIds

  }
*/
  //----------------------------------------------------------------------------

  private def mongoToScala(mongo: DBObject) = {
    val id         = mongo._id.get.toString
    val name       = mongo.as[String]      (NAME)
    val position   = mongo.as[Int]         (POSITION)
    val articleIds = mongo.as[BasicDBList](ARTICLE_IDS).toArray.map(_.toString)

    new Category(id, name, position, articleIds)
  }
}
