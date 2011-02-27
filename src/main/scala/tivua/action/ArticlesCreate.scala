package tivua.action

import xitrum.action.annotation.POST
import tivua.model.Article

@POST("/articles")
class ArticlesCreate extends Application {
  def execute {
    println(fileParam("attachment"))

    val article = new Article
  }
}
