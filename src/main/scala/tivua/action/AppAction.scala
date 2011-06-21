package tivua.action

import xitrum.Action
import xitrum.view.DocType

import tivua.Config
import tivua.action.article.{Index, New}
import tivua.action.auth.CheckFacebookLogin
import tivua.helper.{AppHelper, CategoryHelper}
import tivua.model.Category

trait AppAction extends Action with AppHelper with CategoryHelper {
  beforeFilters("prepareCategories") = () => {
    val categories = Category.all
    Var.rCategories.set(categories)
    categories.find(_.toBeCategorized).foreach(Var.rToBeCategorizedCategory.set(_))

    true
  }

  override def layout = Some(() => DocType.xhtmlTransitional(
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
        {xitrumHead}

        <meta content="text/html; charset=utf-8" http-equiv="content-type" />
        <title>{if (Var.rTitle.isDefined) Config.siteName + " - " + Var.rTitle.get else Config.siteName}</title>

        <link type="image/vnd.microsoft.icon" rel="shortcut icon" href="/favicon.ico" />

        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/fluid960gs/reset.css")} />
        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/fluid960gs/grid.css")} />
        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/fluid960gs/text.css")} />
        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/application.css")} />

        <script type="text/javascript" src="http://tinymce.moxiecode.com/js/tinymce/jscripts/tiny_mce/jquery.tinymce.js"></script>
        <script type="text/javascript" src={urlForPublic("js/application.js")}></script>
      </head>
      <body>
        <div class="container_12">
          <div id="header" class="grid_12">
            <h1><a href={urlFor[Index]}>{Config.siteName}</a></h1>
          </div>

          <div id="content" class="grid_8">
            <div id="flash">{jsFlash}</div>

            {renderedView}
          </div>

          <div id="sidebar" class="grid_3">
            {if(!Var.sFacebookUid.isDefined)
              <a href={"https://www.facebook.com/dialog/oauth?client_id=" + Config.facebookAppId + "&redirect_uri=" + absoluteUrlPrefix + urlFor[CheckFacebookLogin]}>Login with Facebook</a>
            }

            <fb:login-button show-faces="true" width="250"></fb:login-button><br/><br/>

            <p><a href={urlFor[New]}>Create new article</a></p>

            {renderCategories}

            {if (Var.rCategory.isDefined)
              renderCategoryToc(Var.rCategory.get)
            else if (Var.rToBeCategorizedCategory.isDefined)
              renderCategoryToc(Var.rToBeCategorizedCategory.get)
            }
          </div>

          <div class="clear"></div>

          <hr />
          <div id="footer">Powered by <a href="https://github.com/ngocdaothanh/tivua">Tivua</a></div>

          <div id="fb-root"></div>
        </div>

        <script>
          var facebookAppId = '{Config.facebookAppId}';

          <xml:unparsed>
            window.fbAsyncInit = function() {
              FB.init({appId: facebookAppId, status: true, cookie: true, xfbml: true});
              FB.Event.subscribe('auth.login', function(response) {
                window.location.reload();
              });
              FB.Event.subscribe('auth.logout', function(response) {
                window.location.reload();
              });
            };

            (function() {
              var e = document.createElement('script');
              e.type = 'text/javascript';
              e.src = document.location.protocol +
                '//connect.facebook.net/vi_VN/all.js';
              e.async = true;
              document.getElementById('fb-root').appendChild(e);
            }());
          </xml:unparsed>
        </script>

        {jsForView}
      </body>
    </html>
  ))
}
