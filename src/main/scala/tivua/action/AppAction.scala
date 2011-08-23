package tivua.action

import xitrum.Action
import xitrum.view.DocType

import tivua.Config
import tivua.helper.{AppHelper, CategoryHelper, FacebookHelper}
import tivua.model.Category

trait AppAction extends Action with AppHelper with CategoryHelper with FacebookHelper {
  beforeFilters("prepareCategories") = () => {
    val categories = Category.all
    RVar.categories.set(categories)
    categories.find(_.toBeCategorized).foreach(RVar.toBeCategorizedCategory.set(_))

    true
  }

  override def layout = DocType.xhtmlTransitional(
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
        {xitrumHead}

        <meta content="text/html; charset=utf-8" http-equiv="content-type" />
        <title>{if (RVar.title.isDefined) Config.siteName + " - " + RVar.title.get else Config.siteName}</title>

        <link type="image/vnd.microsoft.icon" rel="shortcut icon" href={urlForPublic("../favicon.ico")} />

        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/fluid960gs/reset.css")} />
        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/fluid960gs/grid.css")} />
        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/fluid960gs/text.css")} />
        <link type="text/css" rel="stylesheet" media="all" href={urlForPublic("css/app.css")} />

        <script type="text/javascript" src="http://tinymce.moxiecode.com/js/tinymce/jscripts/tiny_mce/jquery.tinymce.js"></script>
        <script type="text/javascript" src={urlForPublic("js/app.js")}></script>
      </head>
      <body>
        <div class="container_12">
          <div class="grid_12">
            <div id="header">
              <h1><a href={urlFor[ArticleIndex]}>{Config.siteName}</a></h1>
            </div>
          </div>

          <div class="grid_8">
            <div id="mainbar">
              <div id="flash">{jsFlash}</div>

              {renderedView}
            </div>
          </div>

          <div class="grid_4">
            <div id="sidebar">
              {renderLoginLogout}
              {renderChatBox}
              {renderCategories}
              {renderCategoryToc}
            </div>
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
  )
}
