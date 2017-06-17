package com.lkuligin.indexer

import java.io.{BufferedReader, FileReader}
import java.nio.file.FileSystems

import com.lkuligin.indexer.FileObservable.Line
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.{JsString, Json}
import rx.lang.scala.Observable

/**
  * Created by lkuligin on 17/06/2017.
  */
class CommandLineIndexer(args: Array[String],
                         bufferSize: Int,
                         elasticHost: String,
                         elasticPort: Int) extends LazyLogging {
  args.length match {
    case 1 => {
      val dir = System.getProperty("user.dir")
      val basePath = FileSystems.getDefault().getPath(dir)
      val resolvedPath = basePath.resolve(dir + "/" + args(0)).normalize().toString()
      println(resolvedPath)

      logger.info("starting the file processing: " + resolvedPath)

      val elasticConnector = new ElasticIndexConnector(elasticHost, elasticPort)
      val jsonTransformer = EnronJsonTransformerToElastic

      val fileProcessor = new SourceProcessor(() => getObservable(resolvedPath),
        bufferSize,
        jsonTransformer.transformLine(_),
        elasticConnector.bulkIndexing(_))
      val result = fileProcessor.process()
      logger.debug("result " + result)
      elasticConnector.close()
      //findDuplicates(resolvedPath)


    }
    case _ => throw new IllegalArgumentException("exactly 1 argument (raw json path) should be provided!")
  }

  private def getObservable(fileName: String): Observable[Line] = {
    FileObservable(fileName)
  }

  private def findDuplicates(fileName: String) = {
    val reader: Stream[String] = scala.io.Source.fromFile(fileName).getLines.toStream
    val ids: Seq[String] = reader.map(Json.parse(_)).map(json => (json \ "_id" \ "$oid").as[JsString].value)
    println(ids.length)
    val dup = ids.groupBy(identity).collect { case (x,ys) if ys.lengthCompare(1) > 0 => x }
    println(dup)
  }

}

object CommandLineIndexer {
  def apply(args: Array[String], bufferSize: Int, elasticHost: String, elasticPort: Int) =
    new CommandLineIndexer(args, bufferSize, elasticHost, elasticPort)
}
