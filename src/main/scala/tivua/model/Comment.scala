package tivua.model

class Comment(
    var id:        Int,
    var articleId: Int,
    var body:      String,
    var createdAt: DateType,
    var updatedAt: DateType,
    var userId:    Int) {
  def this() = this(0, 0, "", null, null, 0)
}
