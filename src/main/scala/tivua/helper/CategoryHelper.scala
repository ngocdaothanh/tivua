package tivua.helper

import scala.xml.Unparsed

import tivua.action.{CategoryShow, RVar}
import tivua.model.Category

trait CategoryHelper extends AppHelper {
  def renderCategories = {
    <xml:group>
      <h3>Categories</h3>
      <ul>
        {RVar.categories.get.map { c =>
          <li><a href={urlFor[CategoryShow]("id" -> c.id, "nameInUrl" -> titleInUrl(c.name))}>{c.name}</a></li>
        }}
      </ul>
    </xml:group>
  }

  def renderCategoryToc = {
    val categoryo =
      if (RVar.category.isDefined)
        Some(RVar.category.get)
      else if (RVar.toBeCategorizedCategory.isDefined)
        Some(RVar.toBeCategorizedCategory.get)
      else
        None

    if (!categoryo.isEmpty)
      <xml:group>
        <h3>Contents</h3>
        {Unparsed(categoryo.get.toc)}
      </xml:group>
  }
}
