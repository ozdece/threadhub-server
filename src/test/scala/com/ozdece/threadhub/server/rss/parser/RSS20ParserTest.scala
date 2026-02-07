package com.ozdece.threadhub.server.rss.parser

import com.ozdece.threadhub.server.rss.parser.RSS20ParserTest.missingMandatoryChannelFieldFile
import com.ozdece.threadhub.server.rss.parser.RSS20ParserTest.rss20ResourceFile
import org.scalatest.EitherValues
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec

import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class RSS20ParserTest extends AnyFlatSpec with EitherValues with OptionValues {

  "parse function" should "not parse if a mandatory field is missing" in {
    val rss = RSS20Parser.parse(missingMandatoryChannelFieldFile)

    assertResult(RSSXmlParserException("Child tag description does not exists."))(rss.left.value)
  }

  "parse function" should "parse mandatory fields" in {
    val rss = RSS20Parser.parse(rss20ResourceFile).value

    assertResult("Yetkin Report")(rss.title)
    assertResult("https://yetkinreport.com/")(rss.link)
    assertResult("Yetkin Report Siyaset, Ekonomi Haber-Analiz, Yorum")(rss.description)
  }

  "parse function" should "parse optional lastBuildDate and ttl fields" in {
    val rss = RSS20Parser.parse(rss20ResourceFile).value

    assertResult(
      ZonedDateTime.of(LocalDateTime.of(2026, 2, 5, 17, 49, 16), ZoneId.of(ZoneOffset.UTC.getId))
    )(rss.lastBuildDate.value)
    assertResult(None)(rss.ttl)
  }

  "parse function" should "parse image tag" in {
    val rss = RSS20Parser.parse(rss20ResourceFile).value

    val image = rss.image.value

    assertResult(
      "https://yetkinreport.com/wp-content/uploads/2020/03/cropped-yetkinreport-logo-32x32.png"
    )(image.url)
    assertResult("Yetkin Report")(image.title)
    assertResult("https://yetkinreport.com/")(image.link)
    assertResult(32)(image.width.value)
    assertResult(32)(image.height.value)
  }

}

object RSS20ParserTest {
  def rss20ResourceFile: InputStream                = getClass.getResourceAsStream("/RSS20.xml")
  def missingMandatoryChannelFieldFile: InputStream =
    getClass.getResourceAsStream("/MissingMandatoryChannelField.xml")
}
