package com.myproject.analysis.snippet
import net.liftweb._
import http._
import common._
import util.Helpers._
import SHtml._
import js.JE._
import js.JsCmd
import js.JsCmds._
import scala.xml.NodeSeq
import scala.xml.Text
import com.myproject.analysis.comet.ProgressBar
import com.myproject.analysis.model.Constant
import com.myproject.analysis.java.IntelligenceTree
import org.json.JSONObject
import org.json.JSONArray;

object VideoPause extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var id = 0

  var progressData: JSONObject = null
  def dispatch = {
    case "render" => render _
  }
  def render(xhtml: NodeSeq) = {
    def assignID(r: String) = {
      id = r.toInt;
      if (id < 1) {
        id = 0;
      }
    }
    def process(): JsCmd = {
      for (sess <- S.session) {
        val comet = sess.findComet("ProgressBar", Full("VideoPause"))
        if (!comet.isEmpty) {
          (comet.get).asInstanceOf[ProgressBar].schedule()
          (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
        }
      }
      Call("toggleVideoPauseModal") &
        SetHtml("videoPauseResult", Text("")) &
        SetHtml("videoPauseReplace", S.runTemplate(List("templating", "_videoPause_modal")).get);
    }
    bind("videopause", xhtml, "id" -> text("", assignID, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "videoPauseButton")))
  }

  def inProgress() = {
    ds.connect();
    progressData = ds.getVideoPause(id, Constant.inter);
    println("!" + progressData)
  }

  def afterProgress() = {
    //val tableData:JSONArray = progressData.get("tableData").asInstanceOf[JSONArray]
    //val chartData:JSONObject = progressData.get("chartData").asInstanceOf[JSONObject]
    //Call("insertVideoPauseData",Str(tableData.toString()),Str(chartData.toString()))
    Alert("LALALA")
  }
}