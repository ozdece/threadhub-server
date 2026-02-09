package com.ozdece.threadhub.server

import com.ozdece.threadhub.server.rss.parser.RSS20Parser
import zio.Console._
import zio.ZIO
import zio.ZIOAppDefault
import zio.http.Client
import zio.http.Request
import zio.http.URL
import zio.http.ZClientAspect

object Application extends ZIOAppDefault {

  private val followRedirects =
    ZClientAspect.followRedirects(5)((resp, message) => ZIO.logError(message).as(resp))

  private val clientApp: ZIO[Client, Throwable, Unit] = for {
    url    <- ZIO.fromEither(URL.decode("https://yetkinreport.com/feed"))
    client <- ZIO.service[Client].map(_ @@ followRedirects)
    res    <- client.batched(Request.get(url))
    result <- res.body.asString
    rss    <- ZIO.fromEither(RSS20Parser.parse(result))
    _      <- printLine(rss)
  } yield ()

  override def run: ZIO[Any, Throwable, Unit] = clientApp.provide(Client.default)
}
