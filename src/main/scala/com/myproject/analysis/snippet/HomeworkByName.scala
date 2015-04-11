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
object HomeworkByName extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var progressData: JSONObject = null
  var courseName = ""
    
  def dispatch = {
    case "render" => render _
  }
  
  def render(xhtml: NodeSeq) = {
    def assignCourseName(name: String) = {
      courseName = name
    }
    def process(): JsCmd = {
      if (courseName == "") {
        SetHtml("homeworkByNameResult", Text("Invalid course name, please enter again"))
      } else {
        for (sess <- S.session) {
          val comet = sess.findComet("ProgressBar", Full("HomeworkByName"))
          if (!comet.isEmpty) {
            (comet.get).asInstanceOf[ProgressBar].schedule()
            (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
          }
        }
        Call("toggleHomeworkByNameModal()") &
          SetHtml("homeworkByNameResult", Text("")) &
          SetHtml("homeworkByNameReplace", S.runTemplate(List("templating", "_homeworkByName_modal")).get);
      }
    }

    bind("homeworkbyname", xhtml,
        "coursename" ->  text("", assignCourseName, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "homeworkByNameButton")))
  }
  
  def inProgress() = {
    ds.connect();
    progressData = ds.getHomeworkByName(courseName)
  }
  
  def afterProgress() = {
    Call("insertHomeworkByNameData",Str(progressData.toString()))
  }
}