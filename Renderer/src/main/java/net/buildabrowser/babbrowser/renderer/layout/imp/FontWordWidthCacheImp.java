package net.buildabrowser.babbrowser.renderer.layout.imp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.buildabrowser.babbrowser.renderer.layout.FontWordCache;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextRuns;

public class FontWordWidthCacheImp implements FontWordCache {

  private static int MAX_CACHE_SIZE = 10000;

  // LoadedFont is not slottable, and I don't want to make it so
  private Map<LoadedFont, HashMap<String, TextRuns>> wordWidths = new WeakHashMap<>();

  @Override 
  public TextRuns shapeWord(LoadedFont font, String word) {
    Map<String, TextRuns> fontWordWidths = wordWidths.computeIfAbsent(
      // TODO: LinkedHashMap uses more memory, but I need it for removeEldestEntry
      font, _1 -> new LinkedHashMap<>(128, .75f, true) {
        @Override
        protected boolean removeEldestEntry(
          Map.Entry<String, TextRuns> eldest
        ) {
          return size() > MAX_CACHE_SIZE;
        }
      });
    
    return fontWordWidths.computeIfAbsent(
      word, _1 -> font.shape(word));
  }
  
}
