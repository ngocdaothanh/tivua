package tivua.action.article

import xitrum.annotation.GET
import xitrum.validation.Required

import tivua.action.{AppAction, Var}
import tivua.model.Article

@GET(value="/articles/new", first=true)
class New extends AppAction {
  override def execute {
    val article = new Article

    Var.rTitle.set("Create new article")
    renderView(
      <form postback="submit" action={urlForPostbackThis}>
        <label>Title</label>
        <input type="text" name={validate("title", Required)} value={article.title} />
        <br /><br />

        <label>Category</label>
        <ul>
          {Var.rCategories.get.filter(!_.toBeCategorized).map { c =>
            <li>
              <input type="checkbox" name="categories" value={c.id} />
              {c.name}
            </li>
          }}
        </ul>

        <label>Teaser</label>
        <textarea class="tinymce" name={validate("teaser", Required)}>{article.teaser}</textarea>
        <br />

        <label>Body</label>
        <textarea class="tinymce" name={validate("body", Required)}>{article.body}</textarea>
        <br />

        <input type="submit" value="Save" />
      </form>
    )
  }

  override def postback {
    val title  = param("title")
    val teaser = param("teaser")
    val body   = param("body")
  }
}
