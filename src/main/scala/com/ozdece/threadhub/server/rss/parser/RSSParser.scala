package com.ozdece.threadhub.server.rss.parser

trait RSSParser[T] {
  def parse(xmlString: String): Either[RSSXmlParserException, T]
}
