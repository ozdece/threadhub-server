package com.ozdece.threadhub.server.rss

import enumeratum.Enum
import enumeratum.EnumEntry

sealed trait ItemProperty

final case class DcProperty(creator: String)      extends ItemProperty
final case class ContentProperty(encoded: String) extends ItemProperty
final case class MediaProperty(content: Option[MediaContent], thumbnail: Option[MediaThumbnail])
    extends ItemProperty

final case class MediaContent(url: String, medium: MediaContentMedium)
final case class MediaThumbnail(url: String, width: Option[Int], height: Option[Int])

sealed trait MediaContentMedium extends EnumEntry

object MediaContentMedium extends Enum[MediaContentMedium] {
  case object Image      extends MediaContentMedium
  case object Video      extends MediaContentMedium
  case object Audio      extends MediaContentMedium
  case object Document   extends MediaContentMedium
  case object Executable extends MediaContentMedium

  override def values: IndexedSeq[MediaContentMedium] = findValues
}
