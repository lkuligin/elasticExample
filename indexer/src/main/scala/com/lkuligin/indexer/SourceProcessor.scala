package com.lkuligin.indexer

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.ws.WSResponse
import rx.lang.scala.Observable

/**
  * Created by lkuligin on 17/06/2017.
  */
class SourceProcessor(createObservable: () => Observable[String],
                      bufferSize: Int,
                      transformLineOp: String => Option[String],
                      indexLineOp: String => Observable[WSResponse],
                      separator: String = "\n") extends LazyLogging {

  def process() = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val observableStream = createObservable()

    val a: Observable[Int] = observableStream
      .doOnError(e => logger.error("unexpected error while parsing a file" + e.getMessage))
      .flatMap(1, line => {
        transformLineOp(line) match {
          case Some(trasformedLine) => Observable.just(trasformedLine)
          case None => Observable.empty
        }
      })
      .tumblingBuffer(bufferSize)
      .map(_.mkString(separator) + "\n")
      .flatMap(1, indexLineOp(_))
      .map(response => parseRespone(response))
      .onErrorReturn(e => {
        logger.error("indexing unexpected ended up unsuccessfully")
        0
      })

    a.toBlocking.subscribe()

  }

  private def parseRespone(response: WSResponse): Int = response.status match {
    case 200 => {
      logger.debug("sucessfully sent batch to elasticerror")
      1
    }
    case _ => {
      logger.error("bad response from indexer for a batch" + response.status + " " + response.body)
      0
      //TODO add intelligent retries
    }
  }
}
