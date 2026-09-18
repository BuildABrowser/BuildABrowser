package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Font;

import net.buildabrowser.babbrowser.painter.core.FontResourceLoader;
import net.buildabrowser.babbrowser.textshaping.core.FontFamily;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;

public class J2DFontResourceLoader implements FontResourceLoader {

  @Override
  public FontResource load(FontFamily family, FontOptions options) {
    // TODO: Similar to the Skija one, need to respect generic flag
    Font font = new Font(
      translateFont(family),
      options.weight() > 550 ? Font.BOLD : Font.PLAIN,
      Math.round(options.size()));
    if (font.getFamily().equals(Font.DIALOG)) return null;

    // TODO: Also include some default fallbacks, for other languages
    return new J2DFontResource(font, new J2DFontIdentifier(font));
  }

  private String translateFont(FontFamily family) {
    if (!family.isGeneric()) return family.name();
    return switch (family.name()) {
      case "monospace" -> Font.MONOSPACED;
      case "serif" -> Font.SERIF;
      case "sans-serif" -> Font.SANS_SERIF;
      default -> family.name();
    };
  }
  
}
