package colinh.controller

object Errors {
	val routes = Map(
    "404" -> "Errors#error404",
    "500" -> "Errors#error500")
}

class Errors extends Application {
  def error404 {
    render
  }

  def error500 {
    render
  }
}
