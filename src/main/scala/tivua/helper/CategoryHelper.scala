package tivua.helper

import tivua.action.AppAction
import tivua.action.category.Show
import tivua.model.Category

trait CategoryHelper {
  this: AppAction =>

  def renderCategories = {
    val categories = Category.all

    <div>
      Categories:
      <ul>
        {categories.map { c => <li><a href={urlFor[Show]("id" -> c.id, "nameInUrl" -> titleInUrl(c.name))}>{c.name}</a></li> }}
      </ul>
    </div>
  }
}
