package utilities

import java.util.concurrent.atomic.AtomicLong

import com.netaporter.uri.Uri.parse
import org.apache.commons.validator.routines.UrlValidator
import org.apache.commons.validator.routines.UrlValidator.ALLOW_ALL_SCHEMES

class UrlUtilities() {

  def cleanUrl(s: String): String = {
    val uri = parse(s)
    if (uri.scheme.isEmpty) {
      // TODO:.withScheme doesn't work, open a PR?
      val newUri = parse("http://" + s)
      newUri.toURI()
      return newUri.toString()
    }
    return s
  }

  def validateUrl(s: String): Boolean = {
    val urlValidator = new UrlValidator(ALLOW_ALL_SCHEMES);
    return urlValidator.isValid(s)
  }
}
