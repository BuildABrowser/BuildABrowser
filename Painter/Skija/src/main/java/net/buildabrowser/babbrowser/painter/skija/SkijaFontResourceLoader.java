package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontHinting;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.FontSlant;
import io.github.humbleui.skija.FontStyle;
import io.github.humbleui.skija.FontWidth;
import io.github.humbleui.skija.Typeface;
import net.buildabrowser.babbrowser.painter.core.FontResourceLoader;
import net.buildabrowser.babbrowser.textshaping.core.FontFamily;
import net.buildabrowser.babbrowser.textshaping.core.FontIdentifier;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;

public class SkijaFontResourceLoader implements FontResourceLoader {

  private static final FontMgr manager = FontMgr.getDefault();

  // TODO: Also a way to register a new font

  @Override
  public FontResource load(FontFamily family, FontOptions options) {
    // TODO: Respect the isGeneric flag. We need to distinguish between a generic monospace font, and a literal
    // font named "monospace", per the spec, so figure out how to do so in Skija
    FontStyle style = new FontStyle(options.weight(), FontWidth.NORMAL, FontSlant.UPRIGHT);
    Typeface typeface = manager.matchFamilyStyle(family.name(), style);
    if (typeface == null) return null;

    Font font = new Font(typeface, options.size());
    font.setSubpixel(true);
    font.setMetricsLinear(true);
    font.setHinting(FontHinting.NONE);

    FontIdentifier identifier = new SkijaFontIdentifier(font);
    return new SkijaFontResource(typeface, identifier);
  }
  
}
