package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.layout.imp.FontCacheImp;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;

public interface FontCache {

  LoadedFont load(FontOptions fontOptions);

  static FontCache create(FontLoader fontLoader) {
    return new FontCacheImp(fontLoader);
  }

}
