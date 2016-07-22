package utilities

import java.util.concurrent.atomic.AtomicLong

import com.netaporter.uri.Uri.parse
import org.apache.commons.validator.routines.UrlValidator
import org.apache.commons.validator.routines.UrlValidator.ALLOW_ALL_SCHEMES

object UrlUtilities {

  def cleanUrl(s: String): String = {
    if (parse(s).scheme.isEmpty) {
      // TODO: .withScheme doesn't work, open a PR?
      parse("http://" + s).toURI.toString
    } else {
      s
    }
  }

  def validateUrl(s: String): Boolean = {
    val urlValidator = new UrlValidator(ALLOW_ALL_SCHEMES);
    urlValidator.isValid(s)
  }
}
