package com.ozdece.threadhub.server

import com.ozdece.threadhub.server.rss.parser.RSS20Parser
import zio.Console._
import zio.ZIO
import zio.ZIOAppDefault
import zio.ZLayer
import zio.http.Client
import zio.http.Method
import zio.http.Request
import zio.http.Response
import zio.http.Routes
import zio.http.Server
import zio.http.URL
import zio.http.ZClientAspect
import zio.http.handler

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

  private val routes = Routes(
    Method.GET / "v1" / "validate_rss" -> handler(Response.text("hello\n"))
  )

  private val config = Server.Config.default.port(8080)

  override def run: ZIO[Any, Throwable, Unit] = Server
    .install(routes)
    .flatMap(port => printLine(s"Serving at port $port") *> ZIO.never)
    .provide(Server.live, ZLayer.succeed(config))
}
