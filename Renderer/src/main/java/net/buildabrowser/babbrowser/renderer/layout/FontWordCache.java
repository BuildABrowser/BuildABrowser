package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.layout.imp.FontWordWidthCacheImp;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextRuns;

public interface FontWordCache {

  // TODO: May be better to take a OneLoadedFont and return a ShapedText
  // in the future?
  TextRuns shapeWord(LoadedFont font, String word);

  static FontWordCache create() {
    return new FontWordWidthCacheImp();
  }
  
}
