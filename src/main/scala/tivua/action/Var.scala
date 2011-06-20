package tivua.action

import xitrum.RequestVar

import tivua.model.Category

object Var {
  object rTitle    extends RequestVar[String]
  object rCategory extends RequestVar[Category]
}
