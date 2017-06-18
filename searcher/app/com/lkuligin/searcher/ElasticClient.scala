package com.lkuligin.searcher

import com.google.inject.{Inject, Singleton}
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.duration._
import rx.lang.scala.Observable

import scala.concurrent.{Await, Future, duration}

/**
  * Created by lkuligin on 18/06/2017.
  */
@Singleton
class ElasticClient @Inject() (wsClient: WSClient, config: Configuration) extends LazyLogging {
  val basicUrl = "http://" + config.elasticHost + ":" + config.elasticPort

  def getSimpleSearch(query: String,
                      indexName: String,
                      documentType: String,
                      page: Int = 0,
                      resPerPage: Int = 10): Observable[JsValue] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val url = basicUrl + s"/$indexName/$documentType/_search?q=%22$query%22&size=$resPerPage&from=${calculateFrom(page, resPerPage)}"

    Observable.defer(Observable.from(
      wsClient.url(url)
        .withHeaders("Accept" -> "application/json")
        .withHeaders("Accept-Language" -> "en-GB")
        .withRequestTimeout(config.searchTimeout)
        .get()
    )
      .flatMap(parseRespone)
      .map(_.json)
    )
  }

  def calculateFrom(page: Int, resPerPage: Int) = {
    page * resPerPage
  }

  private def parseRespone(response: WSResponse) = {
    if (response.status == 200) {
      logger.debug("sucessful answer from elastic search")
      Observable.just(response)
    }
    else Observable.error(new UnsupportedOperationException(s"Bad response from elastic ${response.status}"))
  }
}
