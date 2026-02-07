package com.ozdece.threadhub.server.rss

sealed trait RSS20Property
sealed trait RSSUpdatePeriod

object RSSUpdatePeriod {
  case object Hourly extends RSSUpdatePeriod
}

final case class SyndicationHint(updatePeriod: RSSUpdatePeriod, updateFrequency: Int)
    extends RSS20Property
