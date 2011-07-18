package tivua.helper

import scala.xml.Unparsed

import tivua.action.{CategoryShow, Var}
import tivua.model.Category

trait CategoryHelper extends AppHelper {
  def renderCategories = {
    <xml:group>
      <h3>Categories</h3>
      <ul>
        {Var.rCategories.get.map { c =>
          <li><a href={urlFor[CategoryShow]("id" -> c.id, "nameInUrl" -> titleInUrl(c.name))}>{c.name}</a></li>
        }}
      </ul>
    </xml:group>
  }

  def renderCategoryToc = {
    val categoryo =
      if (Var.rCategory.isDefined)
        Some(Var.rCategory.get)
      else if (Var.rToBeCategorizedCategory.isDefined)
        Some(Var.rToBeCategorizedCategory.get)
      else
        None

    if (!categoryo.isEmpty)
      <xml:group>
        <h3>Contents</h3>
        {Unparsed(categoryo.get.toc)}
      </xml:group>
  }
}
