package com.ozdece.threadhub.server.rss.parser

import com.ozdece.threadhub.server.datetime.DateTimeParser
import com.ozdece.threadhub.server.rss.Image
import com.ozdece.threadhub.server.rss.RSS20

import java.io.InputStream
import scala.xml.NodeSeq
import scala.xml.XML

object RSS20Parser extends RSSParser[RSS20] {

  override def parse(inputStream: InputStream): Either[RSSXmlParserException, RSS20] = {
    val xml = XML.load(inputStream)

    for {
      channelTag  <- xml.getChildTag("channel")
      title       <- channelTag.getChildTagText("title")
      link        <- channelTag.getChildTagText("link")
      description <- channelTag.getChildTagText("description")
      lastBuildDate = channelTag
        .getChildTagOption("lastBuildDate")
        .flatMap(node => DateTimeParser.parseZonedDateTime(node.text))
      ttl   = channelTag.getChildTagOption("ttl").map(_.text.toInt)
      image = channelTag.getChildTagOption("image").flatMap(toChannelImage)
    } yield RSS20(
      "",
      title,
      link,
      description,
      lastBuildDate,
      ttl,
      image,
      Seq.empty,
      Seq.empty
    )
  }

  private def toChannelImage(imageNode: NodeSeq): Option[Image] = (for {
    url   <- imageNode.getChildTagText("url")
    title <- imageNode.getChildTagText("title")
    link  <- imageNode.getChildTagText("link")
    width  = imageNode.getChildTagOption("width").map(_.text.toInt)
    height = imageNode.getChildTagOption("height").map(_.text.toInt)
  } yield Image(url, title, link, width, height)).toOption

}
