package com.lkuligin.indexer

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import play.api.libs.ws.WSResponse
import rx.lang.scala.Observable

import scala.collection.immutable.Stream.cons
import scala.collection.mutable.ListBuffer

/**
  * Created by lkuligin on 17/06/2017.
  */
class SourceProcessorSpec extends WordSpec with MockitoSugar with MustMatchers {
  val response = mock[WSResponse]

  "jsonReader" when {
    "processing a stream" should {
      "compose correctly" in {
        /*val values = cons("1", {
          throw new IllegalStateException("wrong element of stream accessed!")
          cons("2", Stream.empty)})*/

        val values = "1" #:: "2" #:: "3" #:: "4" #:: "5" #:: Stream.empty
        val output = new ListBuffer[String]()
        val sourceProcessor = new SourceProcessor(() => Observable.from(values), 2,
          x => Some(x+"_"),
          el => {
            output += el
            Observable.just(response)
          },
          ",")
        sourceProcessor.process()
        output.mkString(".") mustBe "1_,2_.3_,4_.5_"
      }

      "skips empty emissions" in {

        val values = "1" #:: "2" #:: "3" #:: "4" #:: "5" #:: Stream.empty
        val output = new ListBuffer[String]()
        val sourceProcessor = new SourceProcessor(() => Observable.from(values), 2,
          x => if (x == "2") None else Some(x+"_"),
          el => {
            output += el
            Observable.just(response)
          }, ",")
        sourceProcessor.process()
        output.mkString(".") mustBe "1_,3_.4_,5_"
      }

    }
  }
}
