package net.buildabrowser.babbrowser.textshaping.core;

import java.util.List;

public interface LoadedFont {

  FontMetrics metrics();

  TextRuns shape(String text);

  static LoadedFont withFallbacks(List<OneLoadedFont> backingFonts) {
    return new FallbackLoadedFont(backingFonts.toArray(new OneLoadedFont[0]));
  }
  
}
