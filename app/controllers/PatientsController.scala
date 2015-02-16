package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json.Json
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentIdentity
import models.Patient


object PatientsController extends Controller with MongoController with securesocial.core.SecureSocial {
  val collection = db[BSONCollection]("patients")

  def index = SecuredAction.async {
      val cursor = collection.find(BSONDocument(), BSONDocument()).cursor[Patient]
      val futureList = cursor.collect[List]()
      futureList.map { patients => Ok(Json.toJson(patients)) }

  }

  
  
}
