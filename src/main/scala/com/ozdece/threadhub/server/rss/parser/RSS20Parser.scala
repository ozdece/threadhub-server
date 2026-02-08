package com.ozdece.threadhub.server.rss.parser

import cats.implicits.catsSyntaxOptionId
import com.ozdece.threadhub.server.datetime.DateTimeParser
import com.ozdece.threadhub.server.rss.ContentProperty
import com.ozdece.threadhub.server.rss.DcProperty
import com.ozdece.threadhub.server.rss.Image
import com.ozdece.threadhub.server.rss.Item
import com.ozdece.threadhub.server.rss.ItemProperty
import com.ozdece.threadhub.server.rss.MediaContent
import com.ozdece.threadhub.server.rss.MediaContentMedium
import com.ozdece.threadhub.server.rss.MediaProperty
import com.ozdece.threadhub.server.rss.MediaThumbnail
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
      title,
      link,
      description,
      lastBuildDate,
      ttl,
      image,
      channelTag.headOption.map(getChannelProperties).getOrElse(Seq.empty[RSS20Property]),
      getItems(channelTag)
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

  private def getItems(channelTag: NodeSeq): Seq[Item] = (channelTag \\ "item").map(toItem)

  private def toItem(itemNode: Node): Item = {
    val categories = (itemNode \\ "category").map(_.text.trim)

    Item(
      itemNode.getChildTagOption("title").map(_.text.trim),
      itemNode.getChildTagOption("link").map(_.text.trim),
      itemNode.getChildTagOption("description").map(_.text.trim),
      itemNode.getChildTagOption("author").map(_.text.trim),
      categories,
      itemNode
        .getChildTagOption("pubDate")
        .flatMap(node => DateTimeParser.parseZonedDateTime(node.text.trim)),
      itemNode.getChildTagOption("guid").map(_.text.trim),
      getItemProperties(itemNode)
    )
  }

  private def getItemProperties(node: Node): Seq[ItemProperty] = {
    val dcProperties      = node.child.filter(_.prefix == "dc")
    val contentProperties = node.child.filter(_.prefix == "content")
    val mediaProperties   = node.child.filter(_.prefix == "media")

    Seq(
      getDcProperty(dcProperties),
      getContentProperty(contentProperties),
      getMediaProperty(mediaProperties)
    ).flatten
  }

  private def getDcProperty(dcNodes: NodeSeq): Option[DcProperty] =
    dcNodes.find(_.label == "creator").map(node => DcProperty(node.text.trim))

  private def getContentProperty(contentNodes: Seq[Node]): Option[ContentProperty] =
    contentNodes.find(_.label == "encoded").map(node => ContentProperty(node.text.trim))

  private def getMediaProperty(nodes: Seq[Node]): Option[MediaProperty] = {
    val content   = nodes.find(_.label == "content").flatMap(getMediaContent)
    val thumbnail = nodes.find(_.label == "thumbnail").flatMap(getMediaThumbnail)

    // If both content and thumbnail empty, then return nothing
    if (content.isEmpty && thumbnail.isEmpty) {
      None
    } else {
      MediaProperty(content, thumbnail).some
    }
  }

  private def getMediaContent(node: Node): Option[MediaContent] = {
    val urlAttribute    = node.attribute("url")
    val mediumAttribute = node.attribute("medium")

    for {
      url       <- urlAttribute.map(_.text.trim)
      mediumStr <- mediumAttribute.map(_.text.trim)
      medium    <- getMediaContentMedium(mediumStr)
    } yield MediaContent(url, medium)
  }

  private def getMediaThumbnail(node: Node): Option[MediaThumbnail] = {
    val width  = node.attribute("width").map(_.text.trim.toInt)
    val height = node.attribute("height").map(_.text.trim.toInt)

    node.attribute("url").map(seqOfNode => MediaThumbnail(seqOfNode.text.trim, width, height))
  }

  private def getMediaContentMedium(mediumStr: String): Option[MediaContentMedium] =
    mediumStr.trim.toUpperCase match {
      case "IMAGE"      => MediaContentMedium.Image.some
      case "VIDEO"      => MediaContentMedium.Video.some
      case "AUDIO"      => MediaContentMedium.Audio.some
      case "DOCUMENT"   => MediaContentMedium.Document.some
      case "EXECUTABLE" => MediaContentMedium.Executable.some
      case _            => None
    }
}
