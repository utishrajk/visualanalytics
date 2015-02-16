object json {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONString, BSONDocumentIdentity}
import reactivemongo.api.Cursor
import scala.concurrent.Future
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

	case class PatientAggregateValues(total: Int, improved: Int)
  case class PatientAggregate(services: String, value: PatientAggregateValues)
  case class PatientAggregateResult(services: String, total: Int, improved: Int)
  //implicit val deliveredServiceFormat = Json.format[PatientAggregateResult]
  case class ComorbidResult(condition: String, count: Int)
  //implicit val comorbitResultFormat = Json.format[ComorbidResult]

  case class ServiceValues(service: String, value: Int)
  //implicit val serviceValuesFormat = Json.format[ServiceValues]
  
  
  val scoreMap = Map.empty[String, PatientAggregateValues]
                                                  //> scoreMap  : scala.collection.mutable.Map[String,json.PatientAggregateValues
                                                  //| ] = Map()
                                                  
 	scoreMap += ("S1" -> PatientAggregateValues(2, 1))
                                                  //> res0: json.scoreMap.type = Map(S1 -> PatientAggregateValues(2,1))

                                                  
	scoreMap += ("S2" -> PatientAggregateValues(3, 2))
                                                  //> res1: json.scoreMap.type = Map(S1 -> PatientAggregateValues(2,1), S2 -> Pat
                                                  //| ientAggregateValues(3,2))
  
  scoreMap += ("S4" -> PatientAggregateValues(1, 1))
                                                  //> res2: json.scoreMap.type = Map(S4 -> PatientAggregateValues(1,1), S1 -> Pat
                                                  //| ientAggregateValues(2,1), S2 -> PatientAggregateValues(3,2))
	val totalMap = scoreMap map { case (k,v) => Json.toJson(scala.collection.immutable.Map("service" -> Json.toJson(k), "value" -> Json.toJson(v.total))) } toSeq
                                                  //> totalMap  : Seq[play.api.libs.json.JsValue] = ArrayBuffer({"service":"S4","
                                                  //| value":1}, {"service":"S1","value":2}, {"service":"S2","value":3})

	val improvedMap = scoreMap map { case (k,v) => Json.toJson(scala.collection.immutable.Map("service" -> Json.toJson(k), "value" -> Json.toJson(v.improved))) } toSeq
                                                  //> improvedMap  : Seq[play.api.libs.json.JsValue] = ArrayBuffer({"service":"S4
                                                  //| ","value":1}, {"service":"S1","value":1}, {"service":"S2","value":2})
                                                  
  Json.toJson(scala.collection.immutable.Map("total" -> totalMap, "improved" -> improvedMap))
                                                  //> res3: play.api.libs.json.JsValue = {"total":[{"service":"S4","value":1},{"s
                                                  //| ervice":"S1","value":2},{"service":"S2","value":3}],"improved":[{"service":
                                                  //| "S4","value":1},{"service":"S1","value":1},{"service":"S2","value":2}]}
                                                  
  val item1 = Json.toJson(scala.collection.immutable.Map("key" -> Json.toJson("Total"), "color" -> Json.toJson("#d62728"), "values" -> Json.toJson(totalMap)))
                                                  //> item1  : play.api.libs.json.JsValue = {"key":"Total","color":"#d62728","val
                                                  //| ues":[{"service":"S4","value":1},{"service":"S1","value":2},{"service":"S2"
                                                  //| ,"value":3}]}
                                                  
  val item2 = Json.toJson(scala.collection.immutable.Map("key" -> Json.toJson("Improved"), "color" -> Json.toJson("#1f77b4"), "values" -> Json.toJson(improvedMap)))
                                                  //> item2  : play.api.libs.json.JsValue = {"key":"Improved","color":"#1f77b4","
                                                  //| values":[{"service":"S4","value":1},{"service":"S1","value":1},{"service":"
                                                  //| S2","value":2}]}
  
  
  val seq = Json.toJson(Seq(item1, item2))        //> seq  : play.api.libs.json.JsValue = [{"key":"Total","color":"#d62728","valu
                                                  //| es":[{"service":"S4","value":1},{"service":"S1","value":2},{"service":"S2",
                                                  //| "value":3}]},{"key":"Improved","color":"#1f77b4","values":[{"service":"S4",
                                                  //| "value":1},{"service":"S1","value":1},{"service":"S2","value":2}]}]
  
	
	 
	 	                                                    
 
}