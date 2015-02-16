package models

import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class DeliveredService (
  gen : String,
  age : Int,
  eth : String,
  lan : String,
  ser : String
)


object DeliveredService {

  implicit val deliveredServiceFormat = Json.format[DeliveredService]

  implicit object deliveredServiceWriter extends BSONDocumentWriter[DeliveredService] {
    def write(deliveredService : DeliveredService) : BSONDocument =
      BSONDocument(
      //"_id" -> deliveredService.id.getOrElse(BSONObjectID.generate),
      "gender_desc" -> deliveredService.gen,
      "age" -> deliveredService.age,
      "ethnicity_desc" -> deliveredService.eth,
      "language_desc" -> deliveredService.lan,
      //"modality_type_desc" -> deliveredService.mod,
      "service_name" -> deliveredService.ser
      )
  }

  implicit object deliveredServiceReader extends BSONDocumentReader[DeliveredService] {
    def read(doc: BSONDocument): DeliveredService = {
      DeliveredService(
        //doc.getAs[BSONObjectID]("_id"),
        catchExeption(doc.getAs[String]("gender_desc")),
        doc.getAs[Int]("age").get,
        catchExeption(doc.getAs[String]("ethnicity_desc")),
        catchExeption(doc.getAs[String]("language_desc")),
        //catchExeption(doc.getAs[String]("modality_type_desc")),
        catchExeption(doc.getAs[String]("service_name"))
      )
    }
  }

  def catchExeption(str : Option[String]) : String = {
    try {
      str.get
    } catch {
      case e : Exception => "NA"
    }
  }

}