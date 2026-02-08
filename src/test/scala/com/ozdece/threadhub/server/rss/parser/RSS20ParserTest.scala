package com.ozdece.threadhub.server.rss.parser

import com.ozdece.threadhub.server.rss.ContentProperty
import com.ozdece.threadhub.server.rss.DcProperty
import com.ozdece.threadhub.server.rss.MediaContentMedium
import com.ozdece.threadhub.server.rss.MediaProperty
import com.ozdece.threadhub.server.rss.Syndication
import com.ozdece.threadhub.server.rss.SyndicationUpdatePeriod
import com.ozdece.threadhub.server.rss.parser.RSS20ParserTest.missingMandatoryChannelFieldFile
import com.ozdece.threadhub.server.rss.parser.RSS20ParserTest.rss20ResourceFile
import com.ozdece.threadhub.server.rss.parser.RSS20ParserTest.theRaceRSSResourceFile
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

  "parse function" should "parse syndication property" in {
    val rss = RSS20Parser.parse(rss20ResourceFile).value

    val syndicationProperty = rss.properties.collect { case s: Syndication => s }.head

    assertResult(SyndicationUpdatePeriod.Hourly)(syndicationProperty.updatePeriod)
    assertResult(1)(syndicationProperty.updateFrequency)
  }

  "parse function" should "parse RSS items" in {
    val rss = RSS20Parser.parse(rss20ResourceFile).value

    val firstItem  = rss.items.head
    val dcProperty = firstItem.properties.collect { case dcProperty: DcProperty => dcProperty }.head

    val firstItemDescription =
      """<p>&#160; TBMM’de Çocuk Hakları Alt Komisyonu’nun hazırladığı, sosyal medyanın 15 yaş altındakilere yasaklanması ve 18 yaş altındakilere ise internetin belli saatlerde sınırlandırılmasını da içeren “Dijital Mecralarda Çocuklarımızı Bekleyen Tehdit ve Riskler” raporu İnsan Haklarını İnceleme Komisyonu tarafından  kabul edildi. Füsun Sarp Nebil tarafından yazılan kapsamlı değerlendirmede bu yaklaşımın ilk bakışta makul görünse de pratikte merkezi</p>
        |<p>The post <a href="https://yetkinreport.com/2026/02/05/cocuklar-ve-dijital-guvenlik/">Çocuklar ve Dijital Güvenlik</a> appeared first on <a href="https://yetkinreport.com">Yetkin Report</a>.</p>""".stripMargin

    assertResult(10)(rss.items.size)
    assertResult("Çocuklar ve Dijital Güvenlik")(firstItem.title.value)
    assertResult("https://yetkinreport.com/2026/02/05/cocuklar-ve-dijital-guvenlik/")(
      firstItem.link.value
    )
    assertResult("YetkinReport")(dcProperty.creator)
    assertResult(
      ZonedDateTime.of(LocalDateTime.of(2026, 2, 5, 17, 49, 16), ZoneId.of(ZoneOffset.UTC.getId))
    )(firstItem.publishedDate.value)
    assertResult(Seq("Hayat", "Siyaset", "çocuklar", "sosyal medya", "Yaş doğrulama"))(
      firstItem.categories
    )
    assertResult("https://yetkinreport.com/?p=46910")(firstItem.guid.value)
    assertResult(firstItemDescription)(firstItem.description.value)
  }

  "parse function" should "parse content and media namespaces of items" in {
    val rss = RSS20Parser.parse(theRaceRSSResourceFile).value

    val firstItem = rss.items.head

    val contentProperty = firstItem.properties.collect { case contentProperty: ContentProperty =>
      contentProperty
    }.head
    val mediaProperty = firstItem.properties.collect { case mediaProperty: MediaProperty =>
      mediaProperty
    }.head

    val expectedContentEncodedValue =
      """<img src="https://www.the-race.com/content/images/2026/02/JaguarTCSRacingChessConcept-09016.png" alt="Why Formula E really is a game of high-speed chess"><p>&#x2018;Formula E is like high-speed chess.&#x2019;&#xA0;</p><p>If I had a quid for every time I&#x2019;d heard that phrase in the Formula E paddock, I&#x2019;d be on Nick Cassidy money this year.</p><p>It&#x2019;s become a bit of a trope. Nevertheless, it&#x2019;s not one without truth or merit because there are genuine similarities in how the two very different sporting disciplines are mastered.</p><p>Don&#x2019;t believe me? Then ask a chess master. Don&#x2019;t have access to one? No problem, we have.</p><figure class="kg-card kg-image-card"><img src="https://www.the-race.com/content/images/2026/02/annaheadshot6.jpg" class="kg-image" alt="Why Formula E really is a game of high-speed chess" loading="lazy" width="2000" height="1333" srcset="https://www.the-race.com/content/images/size/w600/2026/02/annaheadshot6.jpg 600w, https://www.the-race.com/content/images/size/w1000/2026/02/annaheadshot6.jpg 1000w, https://www.the-race.com/content/images/size/w1600/2026/02/annaheadshot6.jpg 1600w, https://www.the-race.com/content/images/size/w2400/2026/02/annaheadshot6.jpg 2400w" sizes="(min-width: 720px) 720px"></figure><p>The 23-year-old Anna Cramling earned her first&#xA0;FIDE rating - FIDE titles&#xA0;are awarded by the international&#xA0;<a href="https://en.wikipedia.org/wiki/Chess?ref=the-race.com">chess</a>&#xA0;governing body&#xA0;<a href="https://en.wikipedia.org/wiki/FIDE?ref=the-race.com">FIDE</a>&#xA0;(Federation Internationale des Echecs for outstanding performance) in February 2013, aged 10. She then earned the&#xA0;Woman FIDE Master (WFM) title in 2018.</p><p>&#x2018;Chess prodigy&#x2019; is a decent description for Cramling as she descends from two chess grandmaster parents, Juan Manuel Bellon Lopez and Pia Cramling. Destined to grace the board, she has not only been one of the world&#x2019;s top players but has also brought chess to new dimensions of popularity through live Twitch streaming.&#xA0;</p><p>Late in 2025, Cramling took part in a promotional film with Jaguar TCS Racing. It caught The Race&#x2019;s attention, not because it was fun, which it really was, but more so because it gave a very different take on the endless links to strategic thinking and multi-adaptational task execution, which stands Formula E apart from most other motorsports.&#xA0;</p><p>Formula E drivers spend a significant amount of time in the driver-in-the-loop simulator (DiL) because at races, they have a lot on their to-do list. Apart from driving the car and racing it against 19 other highly skilled competitors, they have almost corner-by-corner feedback to store and occasionally communicate back to their engineer in real time.</p><p>In addition, they have to ensure their energy consumption is on target and they call this back in cryptological code because radio transmissions are audible to everyone, meaning the transmissions include sensitive information like battery percentage, energy management, and powertrain settings.</p><p>But is it really so simplistic as to say the sports are similar in all but speed? Cramling thinks not.</p><p>&quot;I really think it is really similar in that way, as when you&apos;re playing chess, you&apos;re constantly thinking about a lot of different variations,&#x201D; Cramling tells The Race.</p><p>&#x201C;So, you&apos;re constantly thinking, &#x2018;OK, if I do this move, then this will happen in the position&#x2019; and &#x2018;my opponent can reply in these different ways&apos;. Then within every branch of those different ways, you have to calculate many different variations.&#x201D;&#xA0;</p><p>&#x201C;It&apos;s not just OK to think &#x2018;if I do this, then my opponent might do those&#x2019;.&quot;&#xA0;</p><p>Although you&#x2019;d think that a chess player is operating at a much slower pace, in terms of clarity of thought, you might need to think again.</p><p>&#x201C;It&apos;s many different branches of thought that you need to have all the time,&#x201D; adds Cramling.</p><p>&#x201C;You&apos;re constantly thinking several steps ahead, and you&apos;re also doing that at quite a high speed, because of course, there&apos;s different time controls in chess.</p><p>&#x201C;But, even if we&apos;re talking about classical chess, where the games are quite long, because there&apos;s so many different variations, you may have to make a decision within five or 10 minutes, so you&apos;re actually calculating these different moves at quite a high speed.&#x201D;</p><h2 id="multi-move-psycho-dramas">Multi-move psycho dramas&#xA0;</h2><p>Chess is by its very nature a lot more psychological than motorsport and this is where the real jeopardy lies. But like Formula E, it has a certain correlation in risk and reward, mental peril and hazard.&#xA0;</p><p>&#x201C;It&apos;s, of course, not physical,&#x201D; says Cramling.</p><p>&#x201C;You&apos;re not in any risk of physical damage. But in a way it&apos;s so psychological that you really feel like you are kind of at risk somehow, or that you are in the sense it matters so much. There is no physical risk when it comes to chess. But there is a mental risk.&#x201D;</p><p>Formula E kind of has both. Perhaps the stark case study of this was Sebastien Buemi&#x2019;s infamous Montreal meltdown in July 2017. After a sizable accident in free practice, he fought back through to a brilliant fourth place. But it came at a cost.</p><figure class="kg-card kg-embed-card"><iframe width="200" height="113" src="https://www.youtube.com/embed/oOQnYKDExaM?feature=oembed" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen title="S&#xE9;bastien Buemi In Extraordinary Post-Race Tirade! Hydro-Quebec Montreal ePrix"></iframe></figure><p>Minutes after finishing the race, the then-reigning champion went on a ludicrous rant down the pitlane, in which, comedically, he picked fights with <em>three </em>drivers (Antonio Felix da Costa, Robin Frijns and Daniel Abt) in three minutes. Some still believe that Buemi was actually concussed in the earlier accident. Whatever, it showed that physical and mental toil can have consequences.</p><p>That&#x2019;s not to say chess doesn&#x2019;t come without its physical aspects too. Cramling uses the phrase &#x2018;drained&#x2019; a lot, and with good reason.</p><p>&#x201C;After playing for five or six hours and having your brain constantly be on very high alert,&#x201D; she says.</p><p>&#x201C;You might be in a position where you have five or 10 seconds left on the clock, you have to make very difficult positions that will change everything. Your brain is completely fried.</p><p>&#x201C;And of course, there is a difference between motorsport and chess. But I think it&apos;s more interesting to highlight that, even though on the outside they&apos;re so different, there are all of these similarities.&#x201D;</p><p>While a chess equivalent of Buemi&#x2019;s antics haven&#x2019;t been replicated in such spectacular self-detonation, there have been episodes on the chess circuit. However, an infamous &#x2018;table slam&#x2019; by top player Magnus Carlsen in Norway in 2025 frightened the life out of his victorious rival, Gukesh. </p>
                                       |<!--kg-card-begin: html-->
                                       |<blockquote class="twitter-tweet"><p lang="en" dir="ltr">Current World Champion Gukesh defeats Magnus Carlsen for the first time in classic chess. <a href="https://t.co/4tDFBjon9c?ref=the-race.com">pic.twitter.com/4tDFBjon9c</a></p>&#x2014; non aesthetic things (@PicturesFoIder) <a href="https://twitter.com/PicturesFoIder/status/1929477072482640226?ref_src=twsrc%5Etfw&amp;ref=the-race.com">June 2, 2025</a></blockquote> <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
                                       |<!--kg-card-end: html-->
                                       |<p>A frustrated Carlsen banged his fist on the board, causing Bishops and Queens to jump to attention and a shocked Gukesh to walk away in mixed disbelief and satisfaction on outsmarting his opponent.&#xA0;</p><h2 id="competitive-instincts-shared">Competitive instincts shared</h2><p>When da Costa and Evans met Cramling the mutual respect was pretty evident. Despite the clear differences in their sporting disciplines, it was very evident to Cramling that the competitive desire was there, even if Evans erroneously addressed the classic and most numerous but weakest chess piece as a &apos;prawn&apos;.&#xA0;</p><p>&#x201C;They really cared about beating each other,&#x201D; says Cramling.</p><p>&quot;I can definitely tell that they want to win, that they have this fire within them that you need to be a professional competitor in any sport.</p><p>&#x201C;Also, they were very fast thinkers, and they learned very easily different things, and were able to remember and retain information, and I could tell that they were really listening and understanding. Just the speed of learning as well, I think was quite high in just 20 minutes of filming, which was incredible.&#x201D;</p><p>The driver relies on a vast team of engineers, mechanics, operative personnel, data specialists when exercising their craft. The perceived wisdom and thought is that chess is a completely individual sport. Think again.</p><p>&#x201C;Before really knowing about motorsport, I saw it as if someone crashes the car, it&apos;s up to the individual,&quot; Cramling added. &quot;I think chess, in a similar sense that you actually have a lot of coaches and people preparing you behind the scenes.</p><p>&#x201C;You have people constantly trying to understand what the opponent will play, and they will sit for maybe five or six hours coming up with the best lines to face that person.</p><p>&#x201C;Because if the person that was competing was doing that themselves, they&apos;ll be totally drained for the match. They can&apos;t be the ones doing that. They need other people that are preparing them so they really know what&apos;s going to happen during the match.&#x201D;&#xA0;</p><p>Another similarity, another case in point that the most dynamically opposed sporting quests share many traits.&#xA0;</p><p>&#x201C;I have so much respect for everyone that is competing and that are racing drivers, because I feel like it&apos;s something that I could personally myself never do,&#x201D; says Cramling.&#xA0;</p><p>&#x201C;I don&apos;t even have a licence. But I&apos;ve asked the drivers at Jaguar, and I told them that if they play chess, then they can teach me how to drive.&#x201D;</p><p><em>Images courtesy of Jaguar TCS Racing</em></p>""".stripMargin

    assertResult(expectedContentEncodedValue)(contentProperty.encoded)
    assertResult(
      "https://www.the-race.com/content/images/2026/02/JaguarTCSRacingChessConcept-09016.png"
    )(mediaProperty.content.url)
    assertResult(MediaContentMedium.Image)(mediaProperty.content.medium)
  }

}

object RSS20ParserTest {
  def rss20ResourceFile: InputStream                = getClass.getResourceAsStream("/RSS20.xml")
  def missingMandatoryChannelFieldFile: InputStream =
    getClass.getResourceAsStream("/MissingMandatoryChannelField.xml")
  def theRaceRSSResourceFile: InputStream = getClass.getResourceAsStream("/TheRaceRSS.xml")
}
