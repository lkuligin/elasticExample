package com.lkuligin.indexer

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Created by lkuligin on 17/06/2017.
  */
class EnronJsonTransformerToElasticSpec extends WordSpec with MockitoSugar with MustMatchers {

  val enronTransformer = EnronJsonTransformerToElastic

  "EnronJsonTransformer" when {
    "provided a valid line" should {
      "parses it correctly" in {
        val line = """{"_id":{"$oid":"1"},"a":"1","b":"2"}"""
        val output: Option[String] = enronTransformer.transformLine(line)
        val expected =
          """{"index":{"_id":"1"}}
            |{"a":"1","b":"2"}""".stripMargin
        output mustBe Some(expected)
      }

      "flattens 1-size array" in {
        val line = """{"_id":{"$oid":"1"},"a":"1","cc":["1"]}"""
        val output: Option[String] = enronTransformer.transformLine(line)
        val expected =
          """{"index":{"_id":"1"}}
            |{"a":"1","cc":"1"}""".stripMargin
        output mustBe Some(expected)
      }

      "flattens empty array" in {
        val line = """{"_id":{"$oid":"1"},"a":"1","cc":[]}"""
        val output: Option[String] = enronTransformer.transformLine(line)
        val expected =
          """{"index":{"_id":"1"}}
            |{"a":"1","cc":""}""".stripMargin
        output mustBe Some(expected)
      }

      "flattens long arrays" in {
        val line = """{"_id":{"$oid":"1"},"a":"1","cc":["1","2"]}"""
        val output: Option[String] = enronTransformer.transformLine(line)
        val expected =
          """{"index":{"_id":"1"}}
            |{"a":"1","cc":"1; 2"}""".stripMargin
        output mustBe Some(expected)
      }
    }
  }
}
