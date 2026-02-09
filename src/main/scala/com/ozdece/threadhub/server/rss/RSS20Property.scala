package com.ozdece.threadhub.server.rss

import enumeratum.Enum
import enumeratum.EnumEntry

sealed trait RSS20Property
sealed trait SyndicationUpdatePeriod extends EnumEntry

object SyndicationUpdatePeriod extends Enum[SyndicationUpdatePeriod] {
  case object Hourly  extends SyndicationUpdatePeriod
  case object Daily   extends SyndicationUpdatePeriod
  case object Weekly  extends SyndicationUpdatePeriod
  case object Monthly extends SyndicationUpdatePeriod
  case object Yearly  extends SyndicationUpdatePeriod

  override def values: IndexedSeq[SyndicationUpdatePeriod] = findValues
}

final case class Syndication(updatePeriod: SyndicationUpdatePeriod, updateFrequency: Int)
    extends RSS20Property
