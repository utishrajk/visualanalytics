package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONString, BSONDocumentIdentity}
import models.{Patient, Client, Condition, Service}
import reactivemongo.api.Cursor
import scala.concurrent.Future
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

object ScoreController extends Controller with MongoController with securesocial.core.SecureSocial {
  val clientCollection = db[BSONCollection]("client")
  val conditionCollection = db[BSONCollection]("condition")
  val serviceCollection = db[BSONCollection]("service")
  val errorMessage = scala.collection.immutable.Map("message" -> "No data found")

  case class PatientAggregateValues(total: Int, improved: Int)
  case class PatientAggregate(services: String, value: PatientAggregateValues)
  case class PatientAggregateResult(services: String, total: Int, improved: Int)
  implicit val deliveredServiceFormat = Json.format[PatientAggregateResult]
  case class ComorbidResult(condition: String, count: Int)
  implicit val comorbitResultFormat = Json.format[ComorbidResult]

  def retrieveComorbiditiesAndScore =
    SecuredAction.async {
      implicit request =>

        val json: JsValue = request.body.asJson.get

        val race: Option[String] = (json \ "race").asOpt[String]
        val ageGroup: Option[String] = (json \ "agegroup").asOpt[String]
        val gender: Option[String] = (json \ "gender").asOpt[String]
        val primaryDiagnosis: Option[String] = (json \ "primarydiagnosis").asOpt[String]

        val age = findAgeGroup(ageGroup)
        val filterClient = BSONDocument("race" -> BSONString(race.get), "gender" -> BSONString(gender.get), "age" -> BSONDocument("$gt" -> age._1).add(BSONDocument("$lt" -> age._2)))
        val filterService = BSONDocument("condition_id" -> BSONString(primaryDiagnosis.get))

        val cursorClient = clientCollection.find(filterClient, BSONDocument()).cursor[Client]
        val cursorCondition = conditionCollection.find(BSONDocument(), BSONDocument()).cursor[Condition]
        val cursorService = serviceCollection.find(filterService, BSONDocument()).cursor[Service]

        val futureClient = cursorClient.collect[List]()

        futureClient flatMap {
          case clients => {

            for(client <- clients) {
              println("patientId : " + client.id)
            }

            val clientIds = clients.map(client => client.id)
            val futureCondition = cursorCondition.collect[List]()

            futureCondition flatMap {
              case conditions => {
                {
                  val conditionsFiltered = conditions filter(c => clientIds.contains(c.patient_id)) filter( c => !c.condition_id.equals(primaryDiagnosis.get))

                  val conditionsMap = aggregateConditions(conditionsFiltered)

                  val futureService = cursorService.collect[List]()

                  futureService map {
                    case services => {

                      val scoreMap = Map.empty[String, PatientAggregateValues]

                      aggregateServices(services, clients, scoreMap)

                      if(conditionsMap.size > 0 && scoreMap.size > 0)  {
                        Ok("[" + Json.toJson(transformConditions(conditionsMap)) + "," + Json.toJson(transformList(scoreMap)) + "]")
                      } else {
                        Ok(Json.toJson(errorMessage))
                      }
                    }
                  }
                }
              }
            }
          }
        }
    }

  def transformConditions(conditionsMap : Map[String, Int]) = {

    val values = conditionsMap map { case (k,v) => Json.toJson(scala.collection.immutable.Map("label" -> Json.toJson(k), "value" -> Json.toJson(v)))} toSeq

    Json.toJson(Seq(scala.collection.immutable.Map("key" -> Json.toJson("Comorbid Conditions"), "values" -> Json.toJson(values))))
  }

  //convert the map into the format nvd3 expects.
  def transformList(scoreMap : Map[String, PatientAggregateValues]) = {

    val totalMap = scoreMap map { case (k,v) => Json.toJson(scala.collection.immutable.Map("label" -> Json.toJson(k), "value" -> Json.toJson(v.total))) } toSeq
    val improvedMap = scoreMap map { case (k,v) => Json.toJson(scala.collection.immutable.Map("label" -> Json.toJson(k), "value" -> Json.toJson(v.improved))) } toSeq

    val item1 = Json.toJson(scala.collection.immutable.Map("key" -> Json.toJson("Total"), "color" -> Json.toJson("#6600FF"), "values" -> Json.toJson(totalMap)))
    val item2 = Json.toJson(scala.collection.immutable.Map("key" -> Json.toJson("Improved"), "color" -> Json.toJson("#009933"), "values" -> Json.toJson(improvedMap)))

    Json.toJson(Seq(item1, item2))
  }

  def findAgeGroup(ageGroup : Option[String]) : Tuple2[Int, Int]  = {
    val age = ageGroup.get.toInt

    if(age == 0) (9, 13)
    else if (age == 1) (12, 18)
    else if (age == 2) (17, 25)
    else if (age == 3) (24, 35)
    else if (age == 4) (34, 45)
    else if (age == 5) (44, 55)
    else if (age == 6) (54, 65)
    else (64, 200)
  }

  def aggregateConditions(conditions: List[Condition]) = {
    val conditionsMap = Map.empty[String, Int]

    for(c <- conditions) {
      if(!conditionsMap.contains(c.condition_id)) {
        conditionsMap += c.condition_id -> 1
      } else {
        conditionsMap += c.condition_id -> (conditionsMap.get(c.condition_id).get + 1)
      }
    }

    conditionsMap
  }

  def aggregateServices(services: List[Service], clients: List[Client], scoreMap: scala.collection.mutable.Map[String, ScoreController.PatientAggregateValues]) {
    for (service <- services) {
      for (client <- clients) {

        if (client.id == service.patientId) {

          if (!scoreMap.contains(service.serviceId)) {
            scoreMap += service.serviceId -> PatientAggregateValues(1, client.cgiDischarged)
          } else {
            val key = service.serviceId
            val currentTotal = scoreMap.get(key).get.total
            val currentImproved = scoreMap.get(key).get.improved
            scoreMap += key -> PatientAggregateValues(currentTotal + 1, currentImproved + client.cgiDischarged)
          }
        }
      }
    }
  }
}
