package com.lkuligin.searcher

import com.google.inject.{Inject, Singleton}
import com.typesafe.config.Config

/**
  * Created by lkuligin on 18/06/2017.
  */
@Singleton
class Configuration @Inject() (playConfig: play.api.Configuration) {
  val config: Config = playConfig.underlying

  lazy val elasticPort = config.getInt("elastic.port")
  lazy val elasticHost = config.getString("elastic.host")
  lazy val searchTimeout = config.getLong("elastic.search.timeout")

}
