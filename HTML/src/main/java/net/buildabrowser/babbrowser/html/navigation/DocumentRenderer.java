package net.buildabrowser.babbrowser.html.navigation;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface DocumentRenderer extends Closeable {
  
  boolean shouldRender();

  void recalculateStyles();

  void updateLayout();

  void updateRendering();

  Optional<String> title();

  DocumentChangeListener changeListener();
  
  void onDocumentInvalidated(short invalidationLevel);

  default void onDocumentFocused() {}

  default void onDocumentBlurred() {}

  default void close() throws IOException {}

  default void reactivate() throws IOException {}

  interface DocumentRendererEventListener {

    void onNavigate(URI url);

    void onTitleChanged(String title);

  }

}
