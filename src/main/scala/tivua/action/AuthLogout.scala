package tivua.action

import xitrum.Action

class AuthLogout extends Action {
  override def postback {
    session.reset
    flash("You have logged out.")
    jsRedirectTo[ArticleIndex]
  }
}
