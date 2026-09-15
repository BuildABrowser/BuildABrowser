package net.buildabrowser.babbrowser.browser.net;

import net.buildabrowser.babbrowser.browser.BrowserVersion;
import net.buildabrowser.babbrowser.embedding.standardcommon.util.OSUtil;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.UAChooser;
import net.buildabrowser.babbrowser.renderer.RendererVersion;

public class UAChooserImp implements UAChooser {

  private static final String CHROME_UA_STRING
    = "Mozilla/5.0 ($OS) AppleWebKit/537.36 (KHTML, like Gecko)"
    + " Chrome/146.0.0.0 Safari/537.36";

  @Override
  public String chooseUAString(FetchRequest request) {
    String browserString = BrowserVersion.asUAString();
    String rendererString = RendererVersion.asUAString();

    String osName = OSUtil.getOSName();
    String uaString = switch (request.url().getHost()) {
      // Unfortunately DDG captchas the user with the default UA (and captchas would require JS)
      case "html.duckduckgo.com", "duckduckgo.com" -> CHROME_UA_STRING + " $UA";
      // Unfortunately, HN just shows a page showing "sorry" half the time when using a proper UA string
      case "news.ycombinator.com" -> CHROME_UA_STRING;
      case "whatismybrowser.com", "www.whatismybrowser.com" -> "$UA ($OS)";
      case "buildabrowser.net", "frogfind.de" -> "Mozilla/5.0 ($OS) $UA";
      default -> "Mozilla/5.0($OS) $UA Firefox/149.0 (Not actually Firefox)";
    };

    return uaString
      .replace("$OS", osName)
      .replace("$UA", browserString + " " + rendererString);
  }
  
}
