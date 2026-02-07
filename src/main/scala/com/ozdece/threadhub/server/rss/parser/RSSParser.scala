package com.ozdece.threadhub.server.rss.parser

import java.io.InputStream

trait RSSParser[T] {
  def parse(inputStream: InputStream): Either[RSSXmlParserException, T]
}
