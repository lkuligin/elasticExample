package com.lkuligin.indexer

import com.typesafe.config.ConfigFactory

/**
  * Created by lkuligin on 17/06/2017.
  */
object Configuration {
  lazy val config = ConfigFactory.load()

  lazy val elasticPort = config.getInt("elastic.port")
  lazy val elasticHost = config.getString("elastic.host")
  lazy val batchSize = config.getInt("insertion.batch.size")
  lazy val indexerTimeout = config.getInt("elastic.indexer.timeout")
}
