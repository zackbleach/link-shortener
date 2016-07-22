package service

import model.{Link, Links, LinkPresenter}
import scala.concurrent.Future
import utilities.{ BaseJumper, UrlUtilities }
import scala.util.{Success, Failure}

import scala.concurrent.ExecutionContext.Implicits.global

object LinkService {

  val base = new BaseJumper()

  def addLink(link: Link): Future[String] = {
    val linkExists = Links.get(link.redirectTo)
    linkExists.flatMap(
      exists => if (exists.nonEmpty) {
        Future("Link already exists: " + exists.get.id)
      } else {
        Links.add(link)
      }
    )
  }

  def deleteLink(id: Long): Future[Int] = {
    Links.delete(id)
  }

  def getLink(id: Long): Future[Option[Link]] = {
    Links.get(id)
  }

  def getLinkByShortUrl(shortUrl: String): Future[Option[Link]] = {
    getLink(base.decode(shortUrl))
  }

  def listAllLinks: Future[Seq[Link]] = {
    Links.listAll
  }

  //TODO: should this be here?
  def listAllLinksForDisplay: Future[Seq[LinkPresenter]] = {
    Links.listAll.map(
      _.map {
        link => LinkPresenter(link.id, link.redirectTo, base.encode(link.id))
        } 
    )
  }

}

