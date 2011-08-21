package tivua.action

import xitrum.Action

class AuthLogout extends Action {
  override def postback {
    resetSession
    flash("You have logged out.")
    jsRedirectTo[ArticleIndex]
  }
}
