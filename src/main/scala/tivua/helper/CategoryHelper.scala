package tivua.helper

import scala.xml.Unparsed
import xitrum.Action

import tivua.action.category.Show
import tivua.model.Category

trait CategoryHelper extends AppHelper {
  this: Action =>

  def renderCategories = {
    val categories = Category.all

    <div>
      <h3>Categories</h3>
      <ul>
        {categories.map { c =>
          val name = if (c.name.isEmpty) "Uncategorized" else c.name
          <li><a href={urlFor[Show]("id" -> c.id, "nameInUrl" -> titleInUrl(name))}>{name}</a></li> }
        }
      </ul>
    </div>
  }

  def renderCategoryToc(category: Category) =
    <div>
      <h3>Contents</h3>
      {Unparsed(category.toc)}
    </div>
}
