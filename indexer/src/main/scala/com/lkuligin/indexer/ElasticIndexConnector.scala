package com.lkuligin.indexer

import com.typesafe.scalalogging.LazyLogging
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws._
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}
import rx.lang.scala.Observable

/**
  * Created by lkuligin on 17/06/2017.
  */

class ElasticIndexConnector(elasticHost: String, elasticPort: Int) extends LazyLogging {

  val url = "http://" + elasticHost + ":" + elasticPort
  val config = new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build
  val builder = new AsyncHttpClientConfig.Builder(config)
  val client = new NingWSClient(builder.build)

  def bulkIndexing(batch: String,
                   indexName: String = "enron",
                   indexType: String = "email"): Observable[WSResponse] = {
    val u = createBulkUrlRequest(indexName, indexType)
    import scala.concurrent.ExecutionContext.Implicits.global
    Observable.from(
      client
        .url(u)
        .withHeaders("Accept" -> "application/json")
        .withHeaders("Accept-Language" -> "en-GB")
        .withRequestTimeout(10000)
        .post(batch)
    ).onErrorResumeNext(e => {
      logger.error("error occurred while sending index request to elastic " + e.getMessage)
      Observable.empty
    })
  }

  def close() = client.close()

  private def createBulkUrlRequest(indexName: String, documentType: String) =
    url + "/" + indexName + "/" + documentType + "/_bulk"
  //TODO check for allowed symbols in indexName-documentTye
}
