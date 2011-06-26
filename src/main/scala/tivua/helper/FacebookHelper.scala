package tivua.helper

import java.io.{BufferedReader, InputStreamReader}
import java.net.{URL, URLEncoder}

import scala.io.Source
import scala.util.parsing.json.JSON

import org.jboss.netty.handler.codec.http.QueryStringDecoder

import tivua.Config
import tivua.action.AuthCheckFacebookLogin

/** http://developers.facebook.com/docs/authentication/ */
trait FacebookHelper extends AppHelper {
  // redirect_uri for login and verify must be the same
  // http://stackoverflow.com/questions/4386691/facebook-error-error-validating-verification-code
  lazy val encodedRedirectUri = URLEncoder.encode(absoluteUrlFor[AuthCheckFacebookLogin])

  lazy val facebookLoginUrl =
    "https://www.facebook.com/dialog/oauth?" + 
    "client_id="     + URLEncoder.encode(Config.facebookAppId) +
    "&redirect_uri=" + encodedRedirectUri

  /** @return Facebook uid */
  def facebookVerifyLogin(code: String): Option[String] = {
    try {
      val url =
        "https://graph.facebook.com/oauth/access_token?" +
        "client_id="      + URLEncoder.encode(Config.facebookAppId) +
        "&redirect_uri="  + encodedRedirectUri +
        "&client_secret=" + URLEncoder.encode(Config.facebookAppSecret) +
        "&code="          + code
      val params = Source.fromURL(url, "UTF-8").mkString

      val d     = new QueryStringDecoder("?" + params)
      val token = d.getParameters.get("access_token").get(0)

      val url2 = "https://graph.facebook.com/me?access_token=" + URLEncoder.encode(token)
      val json = Source.fromURL(url2, "UTF-8").mkString

      val map = JSON.parseFull(json).get.asInstanceOf[Map[String, Any]]
      val id = map("id").toString
      Some(id)
    } catch {
      case _ => None
    }
  }
}
