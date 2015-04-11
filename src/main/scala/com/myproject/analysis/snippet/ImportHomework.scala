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

object ImportHomework extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var filePath = "";
  var courseName = "";
  def dispatch = {
    case "render" => render _
  }

  def render(xhtml: NodeSeq) = {
    def assignPath(path: String) = {
      filePath = path;
    }
    
    def assignCourseName (name: String) = {
      courseName = name;
    }

    def process(): JsCmd = {
      if (filePath == "") {
        SetHtml("importHomeworkResult", Text("The file path is invalid, please enter it again"))
      } else {
        for (sess <- S.session) {
          val comet = sess.findComet("ProgressBar", Full("ImportHomework"))
          if (!comet.isEmpty) {
            (comet.get).asInstanceOf[ProgressBar].schedule()
            (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
          }
        }
        Call("toggleImportHomeworkModal()") &
          SetHtml("importHomeworkResult", Text("")) &
          SetHtml("importHomeworkReplace", S.runTemplate(List("templating", "_importHomework_modal")).get);
      }
    }
    bind("importhomework", xhtml, "path" -> text("", assignPath, ("class" -> "form-control")),
        "coursename" -> text("", assignCourseName, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "importHomeworkButton")))
  }

  def inProgress() = {
    ds.connect();
    ds.importHomeworkData(filePath, courseName)
  }
  def afterProgress() = {
    Alert("数据导入完成");
  }
}