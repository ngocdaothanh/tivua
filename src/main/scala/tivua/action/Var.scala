package tivua.action

import xitrum.RequestVar

import tivua.model.Category

object Var {
  object rTitle                 extends RequestVar[String]
  object rCategories            extends RequestVar[Iterable[Category]]
  object rUncategorizedCategory extends RequestVar[Category]
  object rCategory              extends RequestVar[Category]
}
