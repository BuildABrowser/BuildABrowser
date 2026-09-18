package net.buildabrowser.babbrowser.textshaping.core;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class FallbackLoadedFont implements LoadedFont {

  private final OneLoadedFont[] rawFonts;
  private final FontMetrics metrics;

  // TODO: Optimize for fonts outside of the ASCII range
  private final OneLoadedFont[] effectiveFontCache = new OneLoadedFont[256];

  public FallbackLoadedFont(OneLoadedFont[] rawFonts) {
    this.rawFonts = rawFonts;
    this.metrics = rawFonts[0].metrics();
  }

  @Override
  public FontMetrics metrics() {
    return this.metrics;
  }

  @Override 
  public TextRuns shape(String text) {
    if (rawFonts.length == 0) {
      throw new IllegalStateException("Attempt to draw or measure text, but no font was set!");
    }
    if (text.isEmpty()) {
      return new TextRuns(null, 0);
    }
    
    float x = 0; float y = 0;
    List<TextRun> textRuns = new ArrayList<>(1);
    int windowStart = 0;
    float currentX = x;
    float adjustedY = y - metrics.ascent();
    while (windowStart < text.length()) {
      OneLoadedFont currentFont = glyphFont(text.codePointAt(windowStart));
      int windowEnd = endOfConsecutiveFontChars(text, windowStart);
      String windowText = text.substring(windowStart, windowEnd + 1);

      ShapedText shapedText = currentFont.shaper().shapeText(windowText);
      textRuns.add(new TextRun(currentX, adjustedY, shapedText));
      currentX += shapedText.runSize();
      windowStart = windowEnd + 1;
    }

    return new TextRuns(IntrusiveList.fromList(textRuns), currentX);
  }

  private int endOfConsecutiveFontChars(String text, int windowStart) {
    int firstCodepoint = text.codePointAt(windowStart);
    OneLoadedFont initialFont = glyphFont(text.codePointAt(windowStart));
    int windowEnd = windowStart + Character.charCount(firstCodepoint);
    while (windowEnd < text.length()) {
      OneLoadedFont font = glyphFont(text.codePointAt(windowEnd));
      if (font != initialFont) {
        return windowEnd - 1;
      }
      int cp = text.codePointAt(windowEnd);
      windowEnd += Character.charCount(cp);
    }

    return windowEnd - 1;
  }

  private OneLoadedFont glyphFont(int codePoint) {
    // TODO: Look into caching codepoints outside of this, for better i18n performance
    if (
      codePoint < 256
      && effectiveFontCache[codePoint] != null
    ) {
      return effectiveFontCache[codePoint];
    }

    OneLoadedFont rawFont = glyphFontRaw(codePoint);
    if (codePoint < 256) {
      effectiveFontCache[codePoint] = rawFont;
    }

    return rawFont;
  }

  private OneLoadedFont glyphFontRaw(int codePoint) {
    for (OneLoadedFont fontEntry: rawFonts) {
      short glyph = fontEntry.getUTF32Glyph(codePoint);
      if (glyph != 0) return fontEntry;
    }

    OneLoadedFont fallbackFont = findFallbackFont(codePoint);
    if (fallbackFont != null) return fallbackFont;

    return rawFonts[0];
  }

  private OneLoadedFont findFallbackFont(int codePoint) {
    return null;
    /*Typeface primaryTypeface = rawFonts.get(0).font().getTypeface();
    Typeface fallback = FontMgr.getDefault().matchFamilyStyleCharacter(
      primaryTypeface.getFamilyName(),
      primaryTypeface.getFontStyle(),
      null, // TODO: Use lang attribute
      codePoint
    );

    if (fallback == null) return null;
    Font fallbackFont = new Font(fallback, metrics.size());
    LoadedFont fallbackEntry = new LoadedFont(fallbackFont);
    rawFonts.add(fallbackEntry);
    return fallbackEntry;*/
  }

}
