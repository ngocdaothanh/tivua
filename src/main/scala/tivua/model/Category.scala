package tivua.model

import java.util.Date
import scala.collection.mutable.ArrayBuffer

import com.mongodb.BasicDBList
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

class Category(
    var id:       String,
    var name:     String,
    var position: Int,
    var toc:      String,
    var toBeCategorized: Boolean)

object CategoryColl {
  val COLL     = "categories"

  val ID       = "_id"
  val NAME     = "name"
  val POSITION = "position"
  val TOC      = "toc"
}

object Category {
  import CategoryColl._

  private val coll = DB.db(COLL)

  //----------------------------------------------------------------------------

  def all: Iterable[Category] = {
    val cur = coll.find().sort(MongoDBObject("position" -> 1))
    val buffer = ArrayBuffer[Category]()
    for (o <- cur) {
      val c = mongoToScala(o)
      buffer.append(c)
    }
    buffer
  }

  def first(id: String): Option[Category] = coll.findOneByID(new ObjectId(id)).map(mongoToScala)

  //----------------------------------------------------------------------------

  private def mongoToScala(mongo: DBObject) = {
    val id       = mongo._id.get.toString
    val name     = mongo.as[String](NAME)
    val position = mongo.as[Int]   (POSITION)
    val toc      = mongo.as[String](TOC)

    val (name2, toBeCategorized) = if (name.isEmpty) ("To be categorized", true) else (name, false)
    new Category(id, name2, position, toc, toBeCategorized)
  }
}
