package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.painter.core.FontResourceLoader;
import net.buildabrowser.babbrowser.renderer.layout.imp.FontLoaderImp;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextShaperLoader;

public interface FontLoader {

  LoadedFont load(FontOptions options);

  static FontLoader create(
    FontResourceLoader fontResourceLoader,
    TextShaperLoader textShaperLoader
  ) {
    return new FontLoaderImp(
      fontResourceLoader, textShaperLoader);
  }
  
}
