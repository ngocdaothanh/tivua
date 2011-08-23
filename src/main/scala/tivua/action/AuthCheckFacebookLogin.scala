package tivua.action

import xitrum.Action
import xitrum.annotation.GET

import tivua.helper.FacebookHelper

@GET("/auth/check_facebook_login")
class AuthCheckFacebookLogin extends Action with FacebookHelper {
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
            resetSession
            flash("You have successfully logged in.")
            SVar.facebookUid.set(uid)
        }
    }
    redirectTo[ArticleIndex]
  }
}
