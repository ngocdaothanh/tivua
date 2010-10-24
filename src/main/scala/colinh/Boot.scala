package colinh

import xt.server.Server
import xt.framework.XTApp
import xt.middleware.{
  App,
  Squeryl,
  Failsafe, Dispatcher, MethodOverride, ParamsParser, Static}

import colinh.controller._

object Boot {
  def main(args: Array[String]) {
    val routes = Articles.routes

    val errorRoutes = Errors.routes

    val controllerPaths = List("colinh.controller")
    val viewPaths = List("colinh/view")

    // Failsafe should be outside Squeryl.
    // If Failsafe is inside Squeryl, all exceptions would be swallowed.
    var app: App = new XTApp
    app = Squeryl.wrap(app)
    app = Failsafe.wrap(app)
    app = Dispatcher.wrap(app, routes, errorRoutes, controllerPaths, viewPaths)
    app = MethodOverride.wrap(app)
    app = ParamsParser.wrap(app)
    app = Static.wrap(app)

    val http = new Server(app)
    http.start
  }
}
