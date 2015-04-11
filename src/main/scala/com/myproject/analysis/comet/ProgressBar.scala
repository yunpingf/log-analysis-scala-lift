package com.myproject.analysis.comet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.future
import com.myproject.analysis.model.Constant
import com.myproject.analysis.snippet.ImportData
import com.myproject.analysis.snippet.TopVideos
import com.myproject.analysis.snippet.VideoByStudent
import com.myproject.analysis.snippet.VideoPause
import com.myproject.analysis.snippet.VideoMove
import net.liftweb.common.Box.box2Option
import net.liftweb.common.Full
import net.liftweb.http.CometActor
import net.liftweb.http.js.JE.Call
import net.liftweb.http.js.JsCmds.jsExpToJsCmd
import net.liftweb.http.js.JsExp.intToJsExp
import net.liftweb.http.js.JsExp.strToJsExp
import net.liftweb.util.Helpers.longToTimeSpan
import net.liftweb.util.Schedule
import com.myproject.analysis.snippet.VideoInfo

class ProgressBar extends CometActor {
  override def defaultPrefix = Full("pgb")
  case object Tick
  def render = bind("bar" -> bar)
  def bar = (<div class={ "progress-bar " + this.name.get } role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div>)

  Schedule.schedule(this, Tick, 2000L)

  var state = Constant.notStarted
  var isOpen = false
  var counter = 0
  def setState(value: String) = {
    state = value
  }

  def setOpen(value: Boolean) = {
    isOpen = value
  }

  def schedule() {
    Schedule.schedule(this, Tick, 2000L)
  }
  override def lowPriority: PartialFunction[Any, Unit] = {
    case Tick => {
      if (state == Constant.completed) {
        counter = 0
        state = Constant.notStarted
        if (this.name.get == "TopVideos") {
          partialUpdate(TopVideos.afterProgress() &
            Call("progressBarUpdate", counter, this.name.get))

        } else if (this.name.get == "VideoByStudent") {
          partialUpdate(VideoByStudent.afterProgress() &
            Call("progressBarUpdate", counter, this.name.get))
        } else if (this.name.get == "ImportData") {
          partialUpdate(ImportData.afterProgress() &
            Call("progressBarUpdate", counter, this.name.get))
        }
        else if (this.name.get == "VideoPause") {
          partialUpdate(VideoPause.afterProgress() &
            Call("progressBarUpdate", counter, this.name.get))
        }
        else if (this.name.get == "VideoMove") {
          partialUpdate(VideoMove.afterProgress() &
            Call("progressBarUpdate", counter, this.name.get))
        }
        else if (this.name.get == "VideoInfo") {
          partialUpdate(VideoInfo.afterProgress() &
            Call("progressBarUpdate", counter, this.name.get))
        }

      } else if (state == Constant.started) {
        if (counter < 99) {
          counter = counter + 1
          partialUpdate(Call("progressBarUpdate", counter, this.name.get))

          Schedule.schedule(this, Tick, 2000L)
        } else {
          Schedule.schedule(this, Tick, 2000L)
        }
      } else if (state == Constant.notStarted) {

      } else {
        state = Constant.started
        val name = this.name.get
        partialUpdate(Call("progressBarUpdate", 0, name))
        Schedule.schedule(this, Tick, 2000L)
        val f: Future[Unit] = future {
          if (name == "TopVideos") {
            println("Top Videos Just Started")
            TopVideos.inProgress()
          } else if (name == "VideoByStudent") {
            VideoByStudent.inProgress()
          } else if (name == "ImportData") {
            ImportData.inProgress()
          } else if (name == "VideoPause") {
            VideoPause.inProgress()
          } else if (name == "VideoMove") {
            VideoMove.inProgress()
          } else if (name == "VideoInfo") {
            VideoInfo.inProgress
          }
        }
        f onSuccess {
          case u => { state = Constant.completed }
        }
      }
    }
  }
}