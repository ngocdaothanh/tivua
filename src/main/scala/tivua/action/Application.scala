package tivua.action

import xitrum.action.Action
import xitrum.action.view.DocType

import tivua.Config
import tivua.action.article.{Index, New}
import tivua.helper.ApplicationHelper

trait Application extends Action with ApplicationHelper {
  override def layout = Some(() => DocType.xhtmlTransitional(
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
        <meta content="text/html; charset=utf-8" http-equiv="content-type" />
        <title>{Config.siteName} - {at("title")}</title>

        <link type="image/vnd.microsoft.icon" rel="shortcut icon" href="/favicon.ico" />

        <link type="text/css" rel="stylesheet" media="all" href="/public/css/960/reset.css" />
        <link type="text/css" rel="stylesheet" media="all" href="/public/css/960/text.css" />
        <link type="text/css" rel="stylesheet" media="all" href="/public/css/960/960.css" />
        <link type="text/css" rel="stylesheet" media="all" href="/public/css/application.css" />

        {xitrumHead}
        <script type="text/javascript" src="/public/tinymce-3.3.8/jquery.tinymce.js"></script>
        <script type="text/javascript" src="/public/js/application.js"></script>
      </head>
      <body>
        <div class="container_12">
          <div id="header" class="grid_12">
            <h1><a href={urlFor[Index]}>{Config.siteName}</a></h1>
          </div>

          <div id="content" class="grid_8">
            <div id="flash">{jsFlash}</div>

            {at("contentForLayout")}
          </div>

          <div id="sidebar" class="grid_4">
            <a href={urlFor[New]}>Create new article</a>
            <br /><br /><br />
            <fb:name uid="12345"></fb:name>

            <fb:login-button show-faces="true"></fb:login-button>

            <fb:profile-pic uid="12345" facebook-logo="true" />
          </div>

          <div class="clear"></div>

          <div id="footer">Powered by Tivua</div>

          <div id="fb-root"></div>
        </div>

        <script>
          <xml:unparsed>
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
          </xml:unparsed>
        </script>

        {jsForView}
      </body>
    </html>
  ))
}
