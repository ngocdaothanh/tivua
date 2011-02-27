package tivua.action

import xitrum.action.annotation.GET
import tivua.model.Article

@GET(value="/articles/new", first=true)
class ArticlesNew extends Application {
  def execute {
    val article = new Article

    at("title") = "Create new article"
    renderView(
      <form action="/articles" method="post">
        <label>Title</label>
        <input type="text" name="title" value={article.title} />

        <label>Teaser</label>
        <textarea class="tinymce" name="teaser">{article.teaser}</textarea>

        <label>Body</label>
        <textarea class="tinymce article_body" name="body">{article.body}</textarea>

        <input type="submit" value="Save" />
      </form>
    )
  }
}
