package tivua.action.auth

import xitrum.Action
import xitrum.annotation.GET

import tivua.action.article.Index

@GET("/auth/check_facebook_login")
class CheckFacebookLogin extends Action {
  override def execute {
    val codeo = paramo("code")
    codeo match {
      case None =>
        flash("Login to Facebook failed. Please try again.")
        redirectTo[Index]

      case Some(code) =>
        val url =
          "https://graph.facebook.com/oauth/access_token?client_id=" + Config.facebookAppId +
          "&client_secret=" + Config.facebookAppSecret + "&code=" + code
    }
  }
}
