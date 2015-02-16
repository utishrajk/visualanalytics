package models

import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}


case class Service(id: Int,
                   serviceId: String,
                   serviceName: String,
                   patientId: Int,
                   conditionId: String)

object Service {

  implicit val serviceFormat = Json.format[Service]

  implicit object serviceBSONWriter extends BSONDocumentWriter[Service] {
    def write(service: Service): BSONDocument =
      BSONDocument(
        "_id" -> service.id,
        "service_id" -> service.serviceId,
        "service_name" -> service.serviceName,
        "patient_id" -> service.patientId,
        "condition_id" -> service.conditionId)
  }

  implicit object serviceBSONReader extends BSONDocumentReader[Service] {
    def read(doc: BSONDocument): Service =
      Service(
        doc.getAs[Int]("_id").get,
        doc.getAs[String]("service_id").get,
        doc.getAs[String]("service_name").get,
        doc.getAs[Int]("patient_id").get,
        doc.getAs[String]("condition_id").get)
  }
}
