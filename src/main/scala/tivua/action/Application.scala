package tivua.action

import xitrum.action.Action
import xitrum.action.view.DocType

import tivua.helper.ApplicationHelper

trait Application extends Action with ApplicationHelper {
  override def layout = {
    var js = """
      window.fbAsyncInit = function() {
        FB.init({appId: '102713449075', status: true, cookie: true, xfbml: true});
        FB.Event.subscribe('auth.login', function(response) {
          window.location.reload();
        });
        FB.Event.subscribe('auth.logout', function(response) {
          window.location.reload();
        });
      };
      (function() {
        var e = document.createElement('script'); e.async = true;
        e.src = document.location.protocol + '//connect.facebook.net/en_US/all.js';
        document.getElementById('fb-root').appendChild(e);
      }());
    """

    Some(() => DocType.xhtmlTransitional(
      <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
        <head>
          <meta content="text/html; charset=utf-8" http-equiv="content-type" />
          <title>{at("title")}</title>

          <link type="image/vnd.microsoft.icon" rel="shortcut icon" href="/favicon.ico" />

          <link type="text/css" rel="stylesheet" media="all" href="/public/css/960/reset.css" />
          <link type="text/css" rel="stylesheet" media="all" href="/public/css/960/text.css" />
          <link type="text/css" rel="stylesheet" media="all" href="/public/css/960/960.css" />
          <link type="text/css" rel="stylesheet" media="all" href="/public/css/application.css" />

          <script type="text/javascript" src="/public/js/jquery-1.4.3.min.js"></script>
          <script type="text/javascript" src="/public/tinymce-3.3.8/jquery.tinymce.js"></script>
          <script type="text/javascript" src="/public/js/application.js"></script>
        </head>
        <body>
          <div class="container_12">
            <div id="header" class="grid_12">
              <h1><a href="/">Tivua</a></h1>
            </div>

            <div id="content" class="grid_8">
              {at("contentForLayout")}
              <div id="footer">Powered by Tivua</div>
            </div>

            <div id="sidebar" class="grid_4">
              <a href="/articles/make">Create new article</a>
              <br /><br /><br />
              <fb:name uid="12345"></fb:name>

              <fb:login-button show-faces="true"></fb:login-button>

              <fb:profile-pic uid="12345" facebook-logo="true" />
            </div>

            <div id="fb-root"></div>

            <script>{js}</script>
          </div>
        </body>
      </html>
    ))
  }
}
