package net.buildabrowser.babbrowser.painter.java2d;

import net.buildabrowser.babbrowser.painter.core.FontResourceLoader;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public class J2DResourceLoader implements ResourceLoader {

  private final FontResourceLoader fontResourceLoader = new J2DFontResourceLoader();

  @Override
  public ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks
  ) {
    return new Java2DImageLoader(
      mimeType, callbacks,
      // TODO: Need to accept an executor
      task -> new Thread(task).start());
  }

  @Override
  public FontResourceLoader fontResourceLoader() {
    return this.fontResourceLoader;
  }

}
