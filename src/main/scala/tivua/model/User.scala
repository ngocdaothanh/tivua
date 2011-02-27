package tivua.model

class User(
    var id:       Int,
    var username: String) {
  def this() = this(0, "")
}
