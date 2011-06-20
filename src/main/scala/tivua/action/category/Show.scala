package tivua.action.category

import xitrum.annotation.GET
import tivua.action.AppAction

@GET("/categories/:id/:nameInUrl")
class Show extends AppAction {
  override def execute {
    val id = param("id")

  }
}

