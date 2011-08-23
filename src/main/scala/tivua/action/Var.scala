package tivua.action

import xitrum.{RequestVar, SessionVar}

import tivua.model.Category

object RVar {
  object title                   extends RequestVar[String]
  object categories              extends RequestVar[Iterable[Category]]
  object toBeCategorizedCategory extends RequestVar[Category]
  object category                extends RequestVar[Category]
}

object SVar {
  object facebookUid extends SessionVar[String]
}
