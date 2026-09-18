package net.buildabrowser.babbrowser.renderer.layout.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.painter.core.FontResourceLoader;
import net.buildabrowser.babbrowser.renderer.layout.FontLoader;
import net.buildabrowser.babbrowser.textshaping.core.FontFamily;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.OneLoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextShaperLoader;

public class FontLoaderImp implements FontLoader {

  private final FontResourceLoader fontResourceLoader;
  private final TextShaperLoader textShaperLoader;

  public FontLoaderImp(
    FontResourceLoader fontResourceLoader,
    TextShaperLoader textShaperLoader
  ) {
    this.fontResourceLoader = fontResourceLoader;
    this.textShaperLoader = textShaperLoader;
  }

  @Override
  public LoadedFont load(FontOptions options) {
    List<OneLoadedFont> backingFonts = new ArrayList<>(options.families().size());
    for (FontFamily family: options.families()) {
      FontResource resource = fontResourceLoader.load(family, options);
      if (resource == null) continue;
      OneLoadedFont font = textShaperLoader.getOrLoad(resource, options);
      backingFonts.add(font);
    }
    
    return LoadedFont.withFallbacks(backingFonts);
  }
  
}
