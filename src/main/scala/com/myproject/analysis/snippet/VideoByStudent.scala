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
import org.json.JSONObject
import com.myproject.analysis.java.IntelligenceTree
import org.json.JSONArray

object VideoByStudent extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var userId = ""
  var progressData: JSONObject = null
  def dispatch = {
    case "render" => render _
  }
  def render(xhtml: NodeSeq) = {
    def assignUserId(r: String) = {
      userId = r;
    }
    def process(): JsCmd = {
      if (userId == "") {
        SetHtml("videoByStudentResult", Text("This user ID is invalid, please enter it again"))
      } else {
        for (sess <- S.session) {
          val comet = sess.findComet("ProgressBar", Full("VideoByStudent"))
          if (!comet.isEmpty) {
            (comet.get).asInstanceOf[ProgressBar].schedule()
            (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
          }
        }
        Call("toggleVideoByStudentModal") &
          SetHtml("videoByStudentResult", Text("")) &
          SetHtml("videoByStudentReplace", S.runTemplate(List("templating", "_videoByStudent_modal")).get);
      }
    }
    bind("videobystudent", xhtml, "userid" -> text("", assignUserId, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "videoByStudentButton")))
  }
  
  def inProgress() = {
    println("In progress2")
    ds.connect();
    progressData = ds.getUserVideoRank(userId);
  }
  
  def afterProgress() = {
    val tableData:JSONArray = progressData.get("tableData").asInstanceOf[JSONArray]
    val chartData:JSONObject = progressData.get("chartData").asInstanceOf[JSONObject]
    Call("insertVideoByStudentData",Str(tableData.toString()),Str(chartData.toString()))
  }
}