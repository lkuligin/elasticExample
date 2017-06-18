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

  List("input").flatMap(x => argParser(args, List("input")).get(Symbol(x))) match {
    case fileName :: Nil => indexFile(fileName)
    case _ => throw new IllegalArgumentException("Please provide the argument for file name: --input FileName")
    }

  private def indexFile(fileName: String) = {
    val dir = System.getProperty("user.dir")
    val basePath = FileSystems.getDefault().getPath(dir)
    val resolvedPath = basePath.resolve(dir + "/" + fileName).normalize().toString()

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

  private def argParser(args: Array[String],
                        possibleArguments:List[String]): Map[Symbol, String] = {
    type OptionMap = Map[Symbol, String]

    def allowedArgument(arg: String)= ((arg(0) == '-') && (possibleArguments.contains(arg.substring(1))))

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = list match {
      case Nil => map
      case arg :: value :: tail if allowedArgument(arg) =>
        nextOption(map ++ Map(Symbol(arg.substring(1)) -> value), tail)
      case _ =>  throw new IllegalArgumentException("exactly 1 argument (raw json path) should be provided!")
      }
    nextOption(Map(),args.toList)
  }
}

object CommandLineIndexer {
  def apply(args: Array[String], bufferSize: Int, elasticHost: String, elasticPort: Int) =
    new CommandLineIndexer(args, bufferSize, elasticHost, elasticPort)
}
