package com.lkuligin.searcher

import akka.stream.ActorMaterializer
import org.scalatest.{Assertion, MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.mock._
import play.core.server.Server
import play.api.routing.sird._
import play.api.mvc._
import play.api.libs.json._
import play.api.test._
import org.mockito.Mockito._
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, WSCookie, WSResponse}
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}
import play.api.routing.Router
import rx.lang.scala.Observable

import scala.concurrent.Future
import scala.xml.Elem
+

/**
  * Created by lkuligin on 18/06/2017.
  */
class ElasticClientSpec extends PlaySpec with TestHelper with MockitoSugar with MustMatchers {
  val config = mock[Configuration]
  when(config.searchTimeout).thenReturn(100)
  when(config.elasticPort).thenReturn(9300)
  when(config.elasticHost).thenReturn("localhost")


  "ElasticClient" should {
    "use document type and index name and return json" in {
      /*def func1: JsValue = {
        WsTestClient.withClient(client => {
          //val app = GuiceApplicationBuilder().overrides(bind[WSClient].to(client))
          val obs: Observable[JsValue] =
            new ElasticClient(client, config).getSimpleSearch("test", "enronTest", "emailTest")
          getFirstValueFromObservable[JsValue](obs)
        })
      }
      val testUrl = "/enronTest/emailTest/_search?q=%22test%22&size=10&from=0"

      val result = Server.withRouter() {
        case GET(_) => Action {
          Results.Ok(JsString("proper"))
        }
      }
      { implicit port => func1}

      result mustBe JsString("proper")
      */
      }
  }
}
