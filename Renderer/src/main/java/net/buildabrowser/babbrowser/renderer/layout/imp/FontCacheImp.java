package net.buildabrowser.babbrowser.renderer.layout.imp;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import net.buildabrowser.babbrowser.renderer.layout.FontCache;
import net.buildabrowser.babbrowser.renderer.layout.FontLoader;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;

public class FontCacheImp implements FontCache {
  
  private final FontLoader fontLoader;

  private final Map<FontOptions, WeakReference<LoadedFont>> fontCache = new WeakHashMap<>();

  public FontCacheImp(FontLoader fontLoader) {
    this.fontLoader = fontLoader;
  }

  public LoadedFont load(FontOptions fontOptions) {
    WeakReference<LoadedFont> fontMaybe = fontCache.computeIfAbsent(fontOptions, _1 -> new WeakReference<>(fontLoader.load(fontOptions)));
    LoadedFont font = fontMaybe.get();
    if (font != null) return fontMaybe.get();
    font = fontLoader.load(fontOptions);
    fontCache.put(fontOptions, new WeakReference<>(font));
    return font;
  }

}
