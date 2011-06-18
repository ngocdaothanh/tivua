package tivua.helper

trait AppHelper {
  def renderPaginationLinks(numPages: Int, currentPage: Int, urlPrefix: String): Any = {
    if (numPages == 1) return ""

    (1 to numPages).map { p =>
      if (p == currentPage) {
        <b>{p}</b>
      } else {
        <a href={urlPrefix + p}>{p}</a>
      }
    }
  }
}