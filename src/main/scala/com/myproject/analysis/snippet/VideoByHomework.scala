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
import org.json.JSONArray

object VideoByHomework extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var progressData: JSONObject = null
  var homeworkID = ""
  var courseName = ""
  def dispatch = {
    case "render" => render _
  }

  def render(xhtml: NodeSeq) = {
    def assignID(id: String) = {
      homeworkID = id
    }
    
    def assignCourseName(name: String) = {
      courseName = name
    }

    def process(): JsCmd = {
      if (homeworkID == "") {
        SetHtml("videoByHomeworkResult", Text("Invalid ID, please enter again"))
      } else {
        for (sess <- S.session) {
          val comet = sess.findComet("ProgressBar", Full("VideoByHomework"))
          if (!comet.isEmpty) {
            (comet.get).asInstanceOf[ProgressBar].schedule()
            (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
          }
        }
        Call("toggleVideoByHomeworkModal()") &
          SetHtml("videoByHomeworkResult", Text("")) &
          SetHtml("videoByHomeworkReplace", S.runTemplate(List("templating", "_videoByHomework_modal")).get);
      }
    }

    bind("videobyhomework", xhtml,
        "coursename" ->  text("", assignCourseName, ("class" -> "form-control")),
        "id" -> text("", assignID, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "videoByHomeworkButton")))
  }

  def inProgress() = {
    ds.connect();
    progressData = ds.getVideoByHomework(homeworkID, Constant.inter, courseName);
  }
  
  def afterProgress() = {
    val tableData:JSONArray = progressData.get("tableData").asInstanceOf[JSONArray]
    val chartData:JSONObject = progressData.get("chartData").asInstanceOf[JSONObject]
    Call("insertVideoByHomeworkData",Str(tableData.toString()),Str(chartData.toString()))
  }
}