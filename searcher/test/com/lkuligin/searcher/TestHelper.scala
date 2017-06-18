package com.lkuligin.searcher

import rx.lang.scala.Observable

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

/**
  * Created by lkuligin on 18/06/2017.
  */
trait TestHelper {
  def getFirstValueFromObservable[T] (observable: Observable[T]) = {
    val promise = Promise[T]
    observable.first.subscribe(
      r => promise.success(r),
      e => promise.failure(e)
    )
    Await.result(promise.future, Duration.Inf)
  }

}
