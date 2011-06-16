package tivua.action.article

import xitrum.action.annotation.GET

import tivua.action.Application
import tivua.model.Article

@GET(value="/articles/new", first=true)
class New extends Application {
  override def execute {
    val article = new Article

    at("title") = "Create new article"
    renderView(
      <form method="post" enctype="multipart/form-data">
        <label>Title</label>
        <input type="text" name="title" value={article.title} />

        <label>Teaser</label>
        <textarea class="tinymce" name="teaser">{article.teaser}</textarea>

        <label>Body</label>
        <textarea class="tinymce article_body" name="body">{article.body}</textarea>

        <input type="file" name="attachment" />

        <input type="submit" value="Save" />
      </form>
    )
  }
}
