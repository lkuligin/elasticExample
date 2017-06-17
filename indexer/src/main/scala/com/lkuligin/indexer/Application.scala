package com.lkuligin.indexer

import java.nio.file.FileSystemNotFoundException

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by lkuligin on 17/06/2017.
  */
object Application extends App with LazyLogging {

  val config = Configuration

  val cli = CommandLineIndexer(args, config.batchSize, config.elasticHost, config.elasticPort)

}
