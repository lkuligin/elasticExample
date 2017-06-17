package com.lkuligin.indexer

import java.io.{BufferedReader, FileReader}
import com.lkuligin.indexer.FileObservable.Line
import com.typesafe.scalalogging.LazyLogging
import rx.Observer
import rx.observables.SyncOnSubscribe
import scala.util.{Failure, Success, Try}

/**
  * Created by lkuligin on 17/06/2017.
  */
class FileObservable(fileName: String) extends SyncOnSubscribe[BufferedReader, Line]  with LazyLogging {
  override def generateState: BufferedReader = {
    new BufferedReader(new FileReader(fileName))
  }

  override def next(reader: BufferedReader, observer: Observer[_ >: Line]): BufferedReader = {
    Try(Option(reader.readLine())) match {
      case Success(Some(line)) => observer.onNext(line)
      case Success(None) => {
        logger.info("file finished")
        observer.onCompleted()
        this.onUnsubscribe(reader)
      }
      case Failure(e) => {
        println("error occured")
        observer.onError(e)
      }
    }
    reader
  }

  override def onUnsubscribe(reader: BufferedReader): Unit = reader.close()
}

object FileObservable {
  type Line = String

  def apply(fileName: String) =
    rx.lang.scala.JavaConversions.toScalaObservable(rx.Observable.create(new FileObservable(fileName)))
}
