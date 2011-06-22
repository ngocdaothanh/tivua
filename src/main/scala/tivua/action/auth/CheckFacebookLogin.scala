package tivua.action.auth

import xitrum.Action
import xitrum.annotation.GET

import tivua.action.Var
import tivua.action.article.Index
import tivua.helper.FacebookHelper

@GET("/auth/check_facebook_login")
class CheckFacebookLogin extends Action with FacebookHelper {
  override def execute {
    val codeo = paramo("code")
    codeo match {
      case None =>
        flash("Login with Facebook failed. Please try again.")

      case Some(code) =>
        facebookVerifyLogin(code) match {
          case None =>
            flash("Login with Facebook failed. Please try again.")

          case Some(uid) =>
            session.reset
            flash("You have successfully logged in.")
            Var.sFacebookUid.set(uid)
        }
    }
    redirectTo[Index]
  }
}
