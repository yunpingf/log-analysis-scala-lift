package com.myproject.analysis.model

object Constant {
  val dataSourceType = "one"

  val funcCount = "count"
  val funcSum = "sum"
  val funcAvg = "avg"
  val funcMin = "min"
  val funcMax = "max"
  val funcNone = "none"

  val basicLine = "BasicLine"
  val basicColumn = "BasicColumn"
  val pieChart = "PieChart"
  val timeSeries = "TimeSeries"

  val numOfRecords = 1000;

  val typeDate = "date"
  val typeNumber = "number"
  val typeText = "text"

  val datePrecision = List("year", "month", "day", "hour", "minute")
  val ipPrecision = List("0", "1", "2", "3", "4")

  val chartTypes = List("", Constant.basicLine, Constant.basicColumn)//add chart types here

  val started = "started"
  val completed = "completed"
  val notStarted = "notStarted"
  val justBegin = "justBegin"
    
  val inter = 100
}