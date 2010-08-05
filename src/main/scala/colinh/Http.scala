package colinh

import org.jboss.netty.handler.codec.http.HttpMethod._

import xt.server.Server
import xt.framework.XTApp
import xt.middleware.{App, Static,
                      ParamsParser,
                      MethodOverride, Dispatcher, Failsafe, Squeryl}

object Http {
  private val routes =
    (GET,   "/",                    "Articles#index")  ::
    (GET,   "/articles/page/:page", "Articles#index")  ::
    (GET,   "/articles/make",       "Articles#make")   ::
    (POST,  "/articles",            "Articles#create") ::
    (GET,   "/articles/:id",        "Articles#show")   ::
    (GET,   "/articles/:id/edit",   "Articles#edit")   :: Nil

  private val errorRoutes = Map(
    "404" -> "Errors#error404",
    "500" -> "Errors#error500")

  private val controllerPaths = List("colinh.controller")

  def main(args: Array[String]) {
    // Failsafe should be outside Squeryl.
    // If Failsafe is inside Squeryl, all exceptions would be swallowed.
    var app: App = new XTApp
    app = Squeryl.wrap(app)
    app = Failsafe.wrap(app)
    app = Dispatcher.wrap(app, routes, errorRoutes, controllerPaths)
    app = MethodOverride.wrap(app)
    app = ParamsParser.wrap(app)
    app = Static.wrap(app)

    val s = new Server(app)
    s.start
  }
}
