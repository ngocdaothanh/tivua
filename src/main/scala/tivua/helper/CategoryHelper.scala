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
      Categories:
      <ul>
        {categories.map { c => <li><a href={urlFor[Show]("id" -> c.id, "nameInUrl" -> titleInUrl(c.name))}>{c.name}</a></li> }}
        <li>Uncategorized</li>
      </ul>
    </div>
  }

  def renderCategoryToc(category: Category) =
    <div>
      <h2>Contents</h2>
      {Unparsed(category.toc)}
    </div>
}
