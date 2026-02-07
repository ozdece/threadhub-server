package com.ozdece.threadhub.server.datetime

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

object DateTimeParser {
  // RSS 2.0 uses RFC 1123 format
  private val formatter = DateTimeFormatter.RFC_1123_DATE_TIME

  def parseZonedDateTime(dateString: String): Option[ZonedDateTime] = Try(
    ZonedDateTime.parse(dateString, formatter)
  ).toOption
}
