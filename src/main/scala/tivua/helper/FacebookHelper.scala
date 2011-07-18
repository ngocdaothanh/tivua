package tivua.helper

import java.io.{BufferedReader, InputStreamReader}
import java.net.{URL, URLEncoder}

import scala.io.Source
import scala.util.parsing.json.JSON

import org.jboss.netty.handler.codec.http.QueryStringDecoder

import tivua.Config
import tivua.action.{ArticleNew, AuthCheckFacebookLogin, AuthLogout, Var}

/** http://developers.facebook.com/docs/authentication/ */
trait FacebookHelper extends AppHelper {
  // redirect_uri for login and verify must be the same
  // http://stackoverflow.com/questions/4386691/facebook-error-error-validating-verification-code
  lazy val encodedRedirectUri = URLEncoder.encode(absoluteUrlFor[AuthCheckFacebookLogin], "UTF-8")

  lazy val facebookLoginUrl =
    "https://www.facebook.com/dialog/oauth?" +
    "client_id="     + URLEncoder.encode(Config.facebookAppId, "UTF-8") +
    "&redirect_uri=" + encodedRedirectUri

  /** @return Facebook uid */
  def facebookVerifyLogin(code: String): Option[String] = {
    try {
      val url =
        "https://graph.facebook.com/oauth/access_token?" +
        "client_id="      + URLEncoder.encode(Config.facebookAppId, "UTF-8") +
        "&redirect_uri="  + encodedRedirectUri +
        "&client_secret=" + URLEncoder.encode(Config.facebookAppSecret, "UTF-8") +
        "&code="          + code
      val params = Source.fromURL(url, "UTF-8").mkString

      val d     = new QueryStringDecoder("?" + params)
      val token = d.getParameters.get("access_token").get(0)

      val url2 = "https://graph.facebook.com/me?access_token=" + URLEncoder.encode(token, "UTF-8")
      val json = Source.fromURL(url2, "UTF-8").mkString

      val map = JSON.parseFull(json).get.asInstanceOf[Map[String, Any]]
      val id = map("id").toString
      Some(id)
    } catch {
      case _ => None
    }
  }

  def renderLoginLogout =
    if (Var.sFacebookUid.isDefined)
      <table>
        <tr>
          <td><fb:profile-pic uid={Var.sFacebookUid.get} facebook-logo="true" /><br /></td>
          <td style="vertical-align: top; padding-left: 1em">
            <b><fb:name uid={Var.sFacebookUid.get} useyou="false"></fb:name></b><br />
            <a href="#" postback="click" action={urlForPostback[AuthLogout]}>Logout</a><br />
            <a href={urlFor[ArticleNew]}>Create new article</a>
          </td>
        </tr>
      </table>
    else
      <p>
        <img src="http://facebook.com/favicon.ico" />
        <a href={facebookLoginUrl}>Login with Facebook</a>
      </p>
}
