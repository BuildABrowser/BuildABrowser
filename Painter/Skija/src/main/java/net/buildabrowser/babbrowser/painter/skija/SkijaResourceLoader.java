package net.buildabrowser.babbrowser.painter.skija;

import net.buildabrowser.babbrowser.painter.core.FontResourceLoader;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public class SkijaResourceLoader implements ResourceLoader {

  private final FontResourceLoader fontResourceLoader = new SkijaFontResourceLoader();

  @Override
  public ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks
  ) {
    return new SkijaImageLoader(callbacks);
  }

  @Override
  public FontResourceLoader fontResourceLoader() {
    return this.fontResourceLoader;
  }

}
