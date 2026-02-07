package com.ozdece.threadhub.server.rss

import cats.implicits.catsSyntaxOptionId
import cats.syntax.either._

import scala.xml.NodeSeq

package object parser {

  implicit class ScalaXmlUtils(node: NodeSeq) {
    def getChildTag(tagName: String): Either[RSSXmlParserException, NodeSeq] =
      (node \ tagName)
        .asRight[RSSXmlParserException]
        .ensure(RSSXmlParserException(s"Child tag $tagName does not exists."))(_.nonEmpty)

    def getChildTagText(tagName: String): Either[RSSXmlParserException, String] =
      (node \ tagName)
        .asRight[RSSXmlParserException]
        .ensure(RSSXmlParserException(s"Child tag $tagName does not exists."))(_.nonEmpty)
        .map(_.text)

    def getChildTagOption(tagName: String): Option[NodeSeq] =
      (node \ tagName).some.filter(_.nonEmpty)
  }

}
