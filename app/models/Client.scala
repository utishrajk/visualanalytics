package models

import play.api.libs.json.Json
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class Client(id: Int,
                   age: Int,
                   gender: String,
                   race: String,
                   status: String,
                   cgiDischarged: Int)

object Client {

  implicit val clientFormat = Json.format[Client]

  implicit object clientBSONWriter extends BSONDocumentWriter[Client] {
    def write(client: Client): BSONDocument =
      BSONDocument(
        "_id" -> client.id,
        "age" -> client.age,
        "gender" -> client.gender,
        "race" -> client.race,
        "status" -> client.status,
        "cgi_discharged" -> client.cgiDischarged)
  }

  implicit object clientBSONReader extends BSONDocumentReader[Client] {
    def read(doc: BSONDocument): Client =
      Client(
        doc.getAs[Int]("_id").get,
        doc.getAs[Int]("age").get,
        doc.getAs[String]("gender").get,
        doc.getAs[String]("race").get,
        doc.getAs[String]("status").get,
        doc.getAs[Int]("cgi_discharged").get)
  }
}