package models

import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}


case class Condition (id: Int,
                      patient_id : Int,
                      condition_id : String,
                      condition_name: String,
                      is_primary : String)


object Condition {

  implicit val conditionFormat = Json.format[Condition]

  implicit object conditionBSONWriter extends BSONDocumentWriter[Condition] {
    def write(condition: Condition): BSONDocument =
      BSONDocument(
        "_id" -> condition.id,
        "patient_id" -> condition.patient_id,
        "condition_id" -> condition.condition_id,
        "condition_name" -> condition.condition_name,
        "is_primary" -> condition.is_primary)
  }

  implicit object conditionBSONReader extends BSONDocumentReader[Condition] {
    def read(doc: BSONDocument): Condition =
      Condition(
        doc.getAs[Int]("_id").get,
        doc.getAs[Int]("patient_id").get,
        doc.getAs[String]("condition_id").get,
        doc.getAs[String]("condition_name").get,
        doc.getAs[String]("is_primary").get)
  }
}