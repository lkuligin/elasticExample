package com.lkuligin.indexer

import java.io.FileNotFoundException

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import rx.exceptions.Exceptions

import scala.io.Source

/**
  * Created by lkuligin on 17/06/2017.
  */
class FileObservableSpec extends WordSpec with MockitoSugar with MustMatchers {

  "jsonFileReader" when {
    "file not exists" should {
      "throws an FileNotFoundException" in {
        val fileObserbable = FileObservable("file")
        //fileObserbable.toBlocking.subscribe(_ => {},  e => Exceptions.propagate(e))
        assertThrows[FileNotFoundException] {fileObserbable.toBlocking.subscribe(_ => {},
          e => throw e)}
      }
    }

    "file exists" should {
      "lazily iterate line by line" in {
        val filePath = getClass.getResource("/test.json").getPath()
        val fileObserbable = FileObservable(filePath)
        val res = fileObserbable.take(2).toBlocking.toList
        res mustBe List("line1", "line2")
      }
    }
  }

}
