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
import net.liftweb.http.DispatchSnippet
import scala.xml.NodeSeq
import org.json.JSONObject
import org.json.JSONArray
import com.myproject.analysis.comet.ProgressBar
import com.myproject.analysis.model.Constant
import com.myproject.analysis.java.IntelligenceTree

object VideoInfo extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var progressData: JSONObject = null
  var id = 0
  var interval = 0

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
        val comet = sess.findComet("ProgressBar", Full("VideoInfo"))
        if (!comet.isEmpty) {
          (comet.get).asInstanceOf[ProgressBar].schedule()
          (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
        }
      }
      Call("toggleVideoInfoModal") &
        SetHtml("videoInfoResult", Text("")) &
        SetHtml("videoInfoReplace", S.runTemplate(List("templating", "_videoInfo_modal")).get);
    }
    bind("videoinfo", xhtml, "id" -> text("", assignID, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "videoInfoButton")))
  }

  def inProgress() = {
    ds.connect();
    progressData = ds.getVideoInfo(id);
  }
  
  def afterProgress() = {
    Call("insertVideoInfoData",Str(progressData.toString()))
  }
}