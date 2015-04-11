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

object VideoMove extends DispatchSnippet {
	val ds = new IntelligenceTree()
	var id = 0
	var interval = 0

	var progressData:JSONObject = null
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
		
		def assignInter(r: String) = {
		  interval = r.toInt;
		  if (interval < 1) {
		    interval = 100;
		  }
		}
		def process(): JsCmd = {
			for (sess <- S.session) {
				val comet = sess.findComet("ProgressBar", Full("VideoMove"))
				if (!comet.isEmpty) {
					(comet.get).asInstanceOf[ProgressBar].schedule()
					(comet.get).asInstanceOf[ProgressBar].setState(Constant.justBegin)
				}
			}
			Call("toggleVideoMoveModal") &
	          SetHtml("videoMoveResult", Text("")) &
	          SetHtml("videoMoveReplace", S.runTemplate(List("templating", "_videoMove_modal")).get);
		}
		bind("videomove", xhtml, "id" -> text("", assignID, ("class" -> "form-control")),
		    "inter" -> text("", assignInter, ("class" -> "form-control")),
			"submit" -> ajaxSubmit("完成", process, ("class" -> "btn btn-default"), ("id" -> "videoMoveButton")))
	}

	def inProgress() = {
		ds.connect();
		//progressData = ds.getVideoMove(id,interval);
	}

	def afterProgress() = {
		//val tableData:JSONArray = progressData.get("tableData").asInstanceOf[JSONArray]
		//val chartData:JSONObject = progressData.get("chartData").asInstanceOf[JSONObject
	  //Call("insertVideoMoveData_Raphael",Str(progressData.toString()))
	  Call("insertVideoMoveData_Raphael","")
	}
}