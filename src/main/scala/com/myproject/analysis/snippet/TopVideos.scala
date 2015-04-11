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

object TopVideos extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var rank = 0
  var progressData:JSONObject = null
  def dispatch = {
    case "render" => render _
  }
  def render(xhtml: NodeSeq) = {
    def assignRank(r: String) = {
      rank = r.toInt;
      if (rank < 1) {
        rank = 0;
      }
    }
    def process(): JsCmd = {
      if (rank <= 0) {
        SetHtml("topVideosResult", Text("The rank is invalid, please enter it again"))
      } else {
        for (sess <- S.session) {
          val comet = sess.findComet("ProgressBar", Full("TopVideos"))
          if (!comet.isEmpty) {
            (comet.get).asInstanceOf[ProgressBar].schedule()
            (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
          }
        }
        Call("toggleTopVideosModal") &
          SetHtml("topVideosResult", Text("")) &
          SetHtml("topVideosReplace", S.runTemplate(List("templating", "_topVideos_modal")).get);
      }
    }
    bind("topvideos", xhtml, "rank" -> text("", assignRank, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "topVideosButton")))
  }
  
  def inProgress() = {
    ds.connect();
    progressData = ds.getVideoRank(rank);
  }
  
  def afterProgress() = {
    val tableData:JSONArray = progressData.get("tableData").asInstanceOf[JSONArray]
    val chartData:JSONObject = progressData.get("chartData").asInstanceOf[JSONObject]
    Call("insertTopVideosData",Str(tableData.toString()),Str(chartData.toString()))
  }
  
}