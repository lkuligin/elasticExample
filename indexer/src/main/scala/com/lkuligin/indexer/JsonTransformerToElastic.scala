package com.lkuligin.indexer

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json._

import scala.util.control.NonFatal

/**
  * Created by lkuligin on 17/06/2017.
  */
trait JsonTransformerToElastic extends LazyLogging {
  def transformLine(line: String): Option[String]

  protected def flatJsonField(jsValue: JsValue, fieldName: String): JsValue = (jsValue \ fieldName).asOpt[JsArray] match {
    case Some(array) => {
      val flattenedField = array.as[List[JsString]].map(_.value).mkString("; ")
      jsValue.as[JsObject] - fieldName + (fieldName -> JsString(flattenedField))
    }
    case None => jsValue
  }

}

object EnronJsonTransformerToElastic extends JsonTransformerToElastic {
  def transformLine(line: String): Option[String] =  {
    try {
      val json = Json.parse(line)
      val id: String = (json \ "_id" \ "$oid").as[JsString].value
      val body: JsValue = List("cc", "bcc", "to", "recipients", "sender").foldLeft[JsValue](json.as[JsObject] - "_id")(flatJsonField)
      val firstLineJson = JsObject(Seq("index" -> JsObject(Seq("_id" -> JsString(id)))))
      Some(Seq(Json.stringify(firstLineJson), body).mkString("\n"))
    }
    catch {
      case NonFatal(_) => {
        logger.error("Error while parsing line in json occured!")
        None
      }
    }
  }
}
