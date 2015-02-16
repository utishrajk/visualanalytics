package models

import play.api.libs.json.Json

import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

/**
 * Created by utish.rajkarnikar on 2/24/14.
 */
case class ServiceResult (
  service : String,
  total : Int,
  improved : Int,
  percentage : Double

)

object ServiceResult {

  implicit val serviceResult = Json.format[ServiceResult]

  implicit object serviceResultWriter extends BSONDocumentWriter[ServiceResult] {
    def write(serviceResult : ServiceResult) : BSONDocument = {
      BSONDocument(
        "Service" -> serviceResult.percentage,
        "Total" -> serviceResult.total,
        "Improved" -> serviceResult.improved,
        "Percentage" -> serviceResult.percentage

      )
    }
  }

  implicit object serviceResultReader extends BSONDocumentReader[ServiceResult] {
    def read(doc : BSONDocument) : ServiceResult = {
      ServiceResult(
        catchException(doc.getAs[String]("Service")),
        catchException(doc.getAs[Int]("Total")),
        catchException(doc.getAs[Int]("Improved")),
        doc.getAs[Double]("Percentage").get
      )
    }
  }

  def catchException(int : Option[Int]) : Int = {
    try {
      int.get
    } catch {
      case e : Exception => 0
    }
  }

  def catchException(str : Option[String]) : String = {
    try {
      str.get
    } catch {
      case e : Exception => "NA"
    }
  }
}
