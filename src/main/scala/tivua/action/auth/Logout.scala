package tivua.action.auth

import xitrum.Action
import tivua.action.article.Index

class Logout extends Action {
  override def postback {
    session.reset
    flash("You have logged out.")
    jsRedirectTo[Index]
  }
}
