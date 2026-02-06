package com.ozdece.threadhub.server

import java.time.ZonedDateTime

sealed trait RSSUpdatePeriod

object RSSUpdatePeriod {
  case object Hourly extends RSSUpdatePeriod
}

sealed trait RSS20Properties

final case class SyndicationHint(updatePeriod: RSSUpdatePeriod, updateFrequency: Int)
    extends RSS20Properties

sealed trait ItemProperty

final case class DcProperty(creator: String) extends ItemProperty

final case class Image(url: String, title: String, link: String, width: Int, height: Int)

//TODO: Add comments field later
final case class Item(
    id: String,
    title: String,
    link: Option[String],
    description: Option[String],
    author: Option[String],
    category: Seq[String],
    pubDate: Option[ZonedDateTime],
    properties: Seq[ItemProperty]
)

final case class RSS20(
    id: String,
    title: String,
    link: String,
    description: String,
    lastBuildDate: Option[ZonedDateTime],
    ttl: Option[Int],
    image: Option[Image],
    properties: Seq[RSS20Properties],
    items: Seq[Item]
)

object Application extends App {
  println("Hello World!")
}
