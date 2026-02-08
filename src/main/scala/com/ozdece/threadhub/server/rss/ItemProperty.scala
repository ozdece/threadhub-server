package com.ozdece.threadhub.server.rss

sealed trait ItemProperty

final case class DcProperty(creator: String)          extends ItemProperty
final case class ContentProperty(encoded: String)     extends ItemProperty
final case class MediaProperty(content: MediaContent) extends ItemProperty

final case class MediaContent(url: String, medium: MediaContentMedium)

sealed trait MediaContentMedium

object MediaContentMedium {
  case object Image      extends MediaContentMedium
  case object Video      extends MediaContentMedium
  case object Audio      extends MediaContentMedium
  case object Document   extends MediaContentMedium
  case object Executable extends MediaContentMedium
}
