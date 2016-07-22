package controllers

import model.{Link, LinkForm, LinkPresenter}
import play.api.mvc._
import scala.concurrent.Future
import service.LinkService

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import utilities.UrlUtilities

class ApplicationController extends Controller {

  val urlUtils = new UrlUtilities()

  def index() = Action.async { implicit request =>
    LinkService.listAllLinksForDisplay map { links =>
      Ok(views.html.index(LinkForm.form, links))
    }
  }
  def redirectToUrl(shortUrl: String) = Action.async {
    LinkService.getLinkByShortUrl(shortUrl) map { link =>
      if (link.nonEmpty) {
        Redirect(urlUtils.cleanUrl(link.get.redirectTo)) // ensures we have http:// at the beginning if no protocol
      } else {
        Redirect(routes.ApplicationController.index()).flashing("errors" -> "We don't have that link")
      }
  }}

  def addLink() = Action.async { implicit request =>
    LinkForm.form.bindFromRequest.fold(
      errorForm => LinkService.listAllLinksForDisplay map { links =>
        BadRequest(views.html.index(errorForm, links))
      },
      data => {
        val newLink = Link(0, data.redirectTo)
        LinkService.addLink(newLink).map(res => {
          Redirect(routes.ApplicationController.index())
          }
        )
      })
  }

  def deleteLink(id: Long) = Action.async { implicit request =>
    LinkService.deleteLink(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}

