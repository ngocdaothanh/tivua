package colinh.model

object Schema extends org.squeryl.Schema {
  val users    = table[User]("users")
  val articles = table[Article]("articles")
  val comments = table[Comment]("comments")
}
