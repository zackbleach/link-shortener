package controllers

import model.{Link, Links, LinkForm}
import play.api.mvc._
import scala.concurrent.Future

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import utilities.UrlUtilities.cleanUrl

class ApplicationController extends Controller {

  def index() = Action.async { implicit request =>
    Links.listAll map { links =>
      Ok(views.html.index(LinkForm.form, links))
    }
  }

  def redirectToUrl(shortUrl: String) = Action.async {
    Links.getByShortUrl(shortUrl) map { link =>
      if (link.nonEmpty)
        Redirect(cleanUrl(link.get.redirectTo)) // ensures we have http:// at the beginning if no protocol
      else
        Redirect(routes.ApplicationController.index()).flashing("errors" -> "We don't have that link")
    }
  }

  def addLink() = Action.async { implicit request =>
    LinkForm.form.bindFromRequest.fold(
      errorForm => Links.listAll map { links =>
        BadRequest(views.html.index(errorForm, links))
      },
      data => {
        val newLink = Link(0, data.redirectTo)
        Links.add(newLink) map (res => {
          Redirect(routes.ApplicationController.index())
        })
      }
    )
  }

  def deleteLink(id: Long) = Action.async { implicit request =>
    Links.delete(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }
}

