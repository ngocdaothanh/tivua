package tivua.helper

import scala.xml.Unparsed
import xitrum.Action

import tivua.action.category.Show
import tivua.model.Category

trait CategoryHelper extends AppHelper {
  this: Action =>

  def renderCategories = {
    val categories = Category.all

    <xml:group>
      <h3>Categories</h3>
      <ul>
        {categories.map { c =>
          <li><a href={urlFor[Show]("id" -> c.id, "nameInUrl" -> titleInUrl(c.name))}>{c.name}</a></li>
        }}
      </ul>
    </xml:group>
  }

  def renderCategoryToc(category: Category) =
    <xml:group>
      <h3>Contents</h3>
      {Unparsed(category.toc)}
    </xml:group>
}
