package com.lkuligin.searcher

import javax.inject.Inject

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import play.api.mvc.Controller
import play.api.libs.json._
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.mvc.Action
import rx.lang.scala.{Observable, Observer}
import play.api._
import play.api.data.Form
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc._

import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal

/**
  * Created by lkuligin on 18/06/2017.
  */
trait SimpleSearchApiController extends Controller with LazyLogging {
  protected val indexName: String
  protected val es: ElasticClient

  val defaultPage = 0
  val defaultResultsPerPage = 10
  import scala.concurrent.ExecutionContext.Implicits.global

  def search(query: String,
             documentType: Option[String],
             jsonTrasfromOp: JsValue => Observable[JsValue],
             page: Option[Int],
             resPerPage: Option[Int]
            ) = Action.async { _ =>
    documentType match {
      case Some(doc) => {
        val response: Observable[JsValue] = es.getSimpleSearch(query,
          indexName,
          doc,
          page.getOrElse(defaultPage),
          resPerPage.getOrElse(defaultResultsPerPage))
        val promise = Promise[Result]()
        response
          .flatMap(jsonTrasfromOp)
          .map(Ok(_))
          .first
          .subscribe(
            r => promise.success(r),
            e => {
              logger.error("Unexpected exception occured!" + e.getMessage())
              promise.success(NotFound("Unexpected exception"))
            }
          )
        promise.future
      }
      case _ => Future(MethodNotAllowed("Document type not provided!"))
    }
  }

}

@Singleton
class SimpleSearchEnronApiController @Inject() (val es: ElasticClient) extends SimpleSearchApiController {
  val indexName = "enron"
  val defaultDocumentType = "email"

  def search(query: String,
             documentType: Option[String],
             page: Option[Int],
             resPerPage: Option[Int]): Action[AnyContent] = {
    search(query, Some(documentType.getOrElse(defaultDocumentType)), transformResponse, page, resPerPage)
  }

  def index = Action {
    MethodNotAllowed("Document type not provided!")
  }

  def transformResponse(response: JsValue) = {
    val hits = (response \ "hits" \ "total").as[JsNumber].value
    lazy val ids = (response \ "hits" \ "hits" \\ "_id").map(_.as[JsString].value)
    lazy val senders = (response \ "hits" \ "hits" \\ "sender").map(_.as[JsString].value)
    lazy val scores = (response \ "hits" \ "hits" \\ "_score").map(_.as[JsNumber].value)

    val results = (ids zip senders zip scores)
      .map({case ((a,b),c) => EnronEmailSearchReponse(a,b,c)})
      .map(_.toJson)

    val trasformedResponse = JsObject(Seq(
      "hits" -> JsNumber(hits),
      "results" -> JsArray(results)
    ))

    Observable.just(trasformedResponse)
  }
}

case class EnronEmailSearchReponse (
                                   id: String,
                                   sender: String,
                                   score: BigDecimal
                                   ) {
  def toJson = {
    JsObject(Seq("id" -> JsString(id), "sender" -> JsString(sender), "score" -> JsNumber(score)))
  }
}
