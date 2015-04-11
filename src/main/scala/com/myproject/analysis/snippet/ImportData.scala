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

object ImportData extends DispatchSnippet {
  val ds = new IntelligenceTree()
  var filePath = "";
  def dispatch = {
    case "render" => render _
  }
  def render(xhtml: NodeSeq) = {
    def assignPath(path: String) = {
      println(path)
      filePath = path;
    }

    def process(): JsCmd = {
      if (filePath == "") {
        SetHtml("importDataResult", Text("The file path is invalid, please enter it again"))
      } else {
        for (sess <- S.session) {
          val comet = sess.findComet("ProgressBar", Full("ImportData"))
          if (!comet.isEmpty) {
            (comet.get).asInstanceOf[ProgressBar].schedule()
            (comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
          }
        }
        Call("toggleImportDataModal()") &
          SetHtml("importDataResult", Text("")) &
          SetHtml("importDataReplace", S.runTemplate(List("templating", "_importData_modal")).get);
      }
    }
    bind("importdata", xhtml, "path" -> text("", assignPath, ("class" -> "form-control")),
      "submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "importDataButton")))
  }
  
  def inProgress() = {
    ds.connect();
    ds.importVideoData(filePath)
  }
  def afterProgress() = {
     Alert("数据导入完成");
  }
}