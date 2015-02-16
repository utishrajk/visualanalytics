package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future
import reactivemongo.bson.BSONDocument
import play.api.cache.Cached
import scala.collection.mutable.ListBuffer
import play.api.libs.ws.WS
import reactivemongo.bson.BSONString
import reactivemongo.api.collections.default.BSONCollection
import play.api.Play.current
import securesocial.core.java.SecureSocial.SecuredAction


import play.modules.reactivemongo.MongoController
import models._


object DeliveredServiceController extends Controller with MongoController with securesocial.core.SecureSocial {

  def collection = db.collection[BSONCollection]("viewclientservice")

  def patients = db.collection[BSONCollection]("patients")

  def serviceresultCollection = db.collection[BSONCollection]("serviceresult")

  def index = Cached("deliveredAction", 3600) {
    SecuredAction.async {
        val cursor = collection.find(BSONDocument(), BSONDocument()).cursor[DeliveredService]
        val futureList : Future[List[DeliveredService]] = cursor.collect[List](100000)
        futureList.map {
          patients => Ok(Json.toJson(patients))
        }

    }
  }

  def serviceresult = Cached("serviceresult", 3600) {
    SecuredAction.async {
        val cursor = serviceresultCollection.find(BSONDocument(), BSONDocument()).cursor[ServiceResult]
        val futureList = cursor.collect[List]()
        futureList.map {
          results => Ok(Json.toJson(results))
        }
    }
  }

}


