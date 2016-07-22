package model

import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{ Constraint, Invalid, Valid, ValidationError }
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.{ Await, Future }
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import utilities.UrlUtilities.{cleanUrl, validateUrl}
import utilities.BaseJumper

case class Link(id: Long, redirectTo : String) {
  lazy val shortUrl = BaseJumper.encode(id)
}

case class LinkFormData(redirectTo: String)

object LinkForm {

  val validUrlConstraint: Constraint[String] = Constraint("constraints.redirectTo")({
    url =>
    if (!validateUrl(cleanUrl(url)))
      Invalid(Seq(ValidationError("Invalid URL")))
    else
      Valid
  })

  val form = Form(
    mapping(
      "redirectTo" ->
        text.verifying(validUrlConstraint)
    )(LinkFormData.apply)(LinkFormData.unapply)
  )
}

class LinkTableDef(tag: Tag) extends Table[Link](tag, "link") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def redirectTo = column[String]("redirect_to")
  def idx = index("idx_a", (redirectTo), unique = true)

  override def * =
    (id, redirectTo) <> (Link.tupled, Link.unapply)
}

object Links {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val links = TableQuery[LinkTableDef]

  def add(link: Link): Future[String] = {
    val linkExists = Links.get(link.redirectTo)
    linkExists.flatMap(
      exists => if (exists.nonEmpty) {
        Future("Link already exists: " + exists.get.id)
      } else {
        dbConfig.db.run(links += link) map(res => "Link successfully added").recover {
          case ex: Exception => ex.getCause.getMessage
        }
      }
    )
  }

  def delete(id: Long): Future[Int] = 
    dbConfig.db.run(links.filter(_.id === id).delete)

  def get(id: Long): Future[Option[Link]] = 
    dbConfig.db.run(links.filter(_.id === id).result.headOption)

  def get(redirectTo: String): Future[Option[Link]] =
    dbConfig.db.run(links.filter(_.redirectTo === redirectTo).result.headOption)

  def getByShortUrl(shortUrl: String): Future[Option[Link]] =
    get(BaseJumper.decode(shortUrl))

  def listAll: Future[Seq[Link]] =
    dbConfig.db.run(links.result)
}
