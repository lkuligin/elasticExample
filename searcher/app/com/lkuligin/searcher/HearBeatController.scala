package com.lkuligin.searcher

import play.api.mvc.{Action, Controller}

/**
  * Created by lkuligin on 18/06/2017.
  */
class HearBeatController  extends Controller {

  def heartBeatCheck = Action(Ok("I exist"))

}
