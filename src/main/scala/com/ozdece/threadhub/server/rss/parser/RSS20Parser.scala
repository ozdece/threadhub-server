package com.ozdece.threadhub.server.rss.parser

import cats.implicits.catsSyntaxOptionId
import com.ozdece.threadhub.server.datetime.DateTimeParser
import com.ozdece.threadhub.server.rss.Image
import com.ozdece.threadhub.server.rss.RSS20
import com.ozdece.threadhub.server.rss.RSS20Property
import com.ozdece.threadhub.server.rss.Syndication
import com.ozdece.threadhub.server.rss.SyndicationUpdatePeriod

import java.io.InputStream
import scala.xml.Node
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
      channelTag.headOption.map(getChannelProperties).getOrElse(Seq.empty[RSS20Property]),
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

  private def getChannelProperties(channelNode: Node): Seq[RSS20Property] = {
    val syndicationProperties = channelNode.child.filter(_.prefix == "sy")

    Seq[Option[RSS20Property]](
      getSyndicationProperty(syndicationProperties)
    ).flatten
  }

  private def getSyndicationProperty(nodes: Seq[Node]): Option[Syndication] = for {
    updateFrequency <- nodes.find(_.label == "updateFrequency").map(_.text.trim.toInt)
    updatePeriodStr <- nodes.find(_.label == "updatePeriod")
    updatePeriod    <- getUpdatePeriod(updatePeriodStr.text)
  } yield Syndication(updatePeriod, updateFrequency)

  private def getUpdatePeriod(updatePeriodStr: String): Option[SyndicationUpdatePeriod] =
    updatePeriodStr.trim.toUpperCase match {
      case "HOURLY"  => SyndicationUpdatePeriod.Hourly.some
      case "DAILY"   => SyndicationUpdatePeriod.Daily.some
      case "WEEKLY"  => SyndicationUpdatePeriod.Weekly.some
      case "MONTHLY" => SyndicationUpdatePeriod.Monthly.some
      case "YEARLY"  => SyndicationUpdatePeriod.Yearly.some
      case _         => None
    }

}
