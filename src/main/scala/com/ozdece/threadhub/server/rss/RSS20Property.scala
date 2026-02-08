package com.ozdece.threadhub.server.rss

sealed trait RSS20Property
sealed trait SyndicationUpdatePeriod

object SyndicationUpdatePeriod {
  case object Hourly  extends SyndicationUpdatePeriod
  case object Daily   extends SyndicationUpdatePeriod
  case object Weekly  extends SyndicationUpdatePeriod
  case object Monthly extends SyndicationUpdatePeriod
  case object Yearly  extends SyndicationUpdatePeriod
}

final case class Syndication(updatePeriod: SyndicationUpdatePeriod, updateFrequency: Int)
    extends RSS20Property
