package net.buildabrowser.babbrowser.painter.core;

public interface ResourceLoader {

  ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks
  );

  FontResourceLoader fontResourceLoader();

}
