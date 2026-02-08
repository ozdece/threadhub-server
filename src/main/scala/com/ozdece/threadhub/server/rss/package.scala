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
      title: Option[String],
      link: Option[String],
      description: Option[String],
      author: Option[String],
      categories: Seq[String],
      publishedDate: Option[ZonedDateTime],
      guid: Option[String],
      properties: Seq[ItemProperty]
  )

  final case class RSS20(
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
