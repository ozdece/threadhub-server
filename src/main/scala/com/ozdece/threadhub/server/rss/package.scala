package com.ozdece.threadhub.server

import java.time.ZonedDateTime

package object rss {

  final case class Image(
      url: String,
      title: String,
      link: String,
      width: Option[Int],
      height: Option[Int]
  )

  // TODO: Add comments field later
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
      properties: Seq[RSS20Property],
      items: Seq[Item]
  )
}
