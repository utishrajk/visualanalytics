package models

import play.api.libs.json.Json
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.BSONStringHandler
import reactivemongo.bson.Producer.nameValue2Producer
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat


case class Patient(id: Option[BSONObjectID], patientID: Int, race: String, gender: String, age: Int, date: String, score: Int, medicalProblems: String, servicesRendered: String)

object Patient {
  /** serialize/Deserialize a patient into/from JSON value */
  implicit val patientFormat = Json.format[Patient]

  /** serialize a patient into a BSON */
  implicit object patientBSONWriter extends BSONDocumentWriter[Patient] {
    def write(patient: Patient): BSONDocument =
      BSONDocument(
        "_id" -> patient.id.getOrElse(BSONObjectID.generate),
        "PatientID" -> patient.patientID,
        "Race" -> patient.race,
        "Gender" -> patient.gender,
        "Age" -> patient.age,
        "Date" -> patient.date,
        "Score" -> patient.score,
        "MedicalProblems" -> patient.medicalProblems,
        "ServicesRendered" -> patient.servicesRendered)
  }

  /** deserialize a patient from a BSON */
  implicit object patientBSONReader extends BSONDocumentReader[Patient] {
    def read(doc: BSONDocument): Patient =
      Patient(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[Int]("PatientID").get,
        doc.getAs[String]("Race").get,
        doc.getAs[String]("Gender").get,
        doc.getAs[Int]("Age").get,
        doc.getAs[String]("Date").get,
        doc.getAs[Int]("Score").get,
        doc.getAs[String]("MedicalProblems").get,
        doc.getAs[String]("ServicesRendered").get)
  }
}