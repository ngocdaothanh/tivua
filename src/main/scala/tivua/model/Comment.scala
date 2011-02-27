package tivua.model

class Comment(
    var id:        Int,
    var articleId: Int,
    var body:      String,
    var createdAt: Int,
    var updatedAt: Int,
    var userId:    Int) {
  def this() = this(0, 0, "", 0, 0, 0)
}
