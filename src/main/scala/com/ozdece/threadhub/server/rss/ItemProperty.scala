package com.ozdece.threadhub.server.rss

sealed trait ItemProperty

final case class DcProperty(creator: String) extends ItemProperty
