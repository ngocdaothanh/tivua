package tivua

import xitrum.server.Server

object Boot {
  def main(args: Array[String]) {
    val server = new Server
    server.start
  }
}
