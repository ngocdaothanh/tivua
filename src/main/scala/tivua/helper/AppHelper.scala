package tivua.helper

import scala.collection.immutable.Range.Inclusive

import xitrum.Action
import xitrum.comet.CometPublishAction
import xitrum.validation.Required

import tivua.action.Chat

trait AppHelper extends Action {
  /**
   * @param urlFormat Typically "/pages/%s"
   */
  def renderPaginationLinks(numPages: Int, currentPage: Int, urlFormat: String, wing: Int = 3): Any = {
    if (numPages == 1) return ""

    val range1 = 1 to (if (wing + 1 > numPages) numPages else wing + 1)
    val range2 = (if (currentPage - wing < 1) 1 else currentPage - wing) to (if (currentPage + wing > numPages) numPages else currentPage + wing)
    val range3 = (if (numPages - wing < 1) 1 else numPages - wing) to numPages

    def visualize(range: Inclusive) = {
      range.map { p =>
        if (p == currentPage)
          <b> {p} </b>
        else
          <a href={urlFormat.format(p)}>{p}</a>
      }
    }

    <div class="pagination">
      {visualize(range1)}
      {if (range2.min > range1.max + 1) <span> ... {visualize(range2)}</span> else visualize((range1.max + 1) to range2.max)}
      {if (range3.min > range2.max + 1) <span> ... {visualize(range3)}</span> else visualize((range2.max + 1) to range3.max)}
    </div>
  }

  def titleInUrl(title: String) = unAccent(title).replace('/', '-').replace(' ', '-')

  def renderChatBox = {
    jsCometGet("chat", """
      function(channel, timestamp, body) {
        var wasScrollAtBottom = xitrum.isScrollAtBottom('#chatOutput');

        var escaped = $('<div/>').text(body.chatInput[0]).html();
        $('#chatOutput').append('- ' + escaped + '<br />');

        if (wasScrollAtBottom) xitrum.scrollToBottom('#chatOutput');
      }
    """)
    <xml:group>
      <h2>Chat</h2>
      <div id="chatOutput"></div>
      <form postback="submit" action={urlForPostback[CometPublishAction]} after="function() { $('#chatInput').attr('value', '') }">
        <input type="hidden" name={validate("channel")} value="chat" />
        <input type="text" id="chatInput" name={validate("chatInput", Required)} />
      </form>
    </xml:group>
  }

  //----------------------------------------------------------------------------

  private def unAccent(s: String) = {
    import java.text.Normalizer
    import java.util.regex.Pattern

    // http://www.rgagnon.com/javadetails/java-0456.html
    val tmp = Normalizer.normalize(s, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    val ret = pattern.matcher(tmp).replaceAll("")

    // The above does not work for đ and Đ
    ret.replace('đ', 'd').replace('Đ', 'D')
  }
}
