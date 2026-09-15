package net.buildabrowser.babbrowser.embedding.standardcommon.imp;

import net.buildabrowser.babbrowser.embedding.standardcommon.util.OSUtil;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.UAChooser;
import net.buildabrowser.babbrowser.renderer.RendererVersion;

public class BasicUAChooserImp implements UAChooser {

  @Override
  public String chooseUAString(FetchRequest request) {
    String rendererString = RendererVersion.asUAString();

    String osName = OSUtil.getOSName();
    String uaString = switch (request.url().getHost()) {
      case "whatismybrowser.com", "www.whatismybrowser.com" -> "$UA ($OS)";
      case "buildabrowser.net" -> "Mozilla/5.0 ($OS) $UA";
      default -> "Mozilla/5.0($OS) $UA Firefox/149.0 (Not actually Firefox)";
    };

    return uaString
      .replace("$OS", osName)
      .replace("$UA", rendererString);
  }
  
}
