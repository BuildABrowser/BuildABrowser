package net.buildabrowser.babbrowser.renderer.uistate;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.uistate.event.FrameEventListener;
import net.buildabrowser.babbrowser.renderer.uistate.imp.FrameImp;

public interface Frame extends Closeable {

  FrameAPIs frameAPIs();

  RenderingEngine renderingEngine();

  GraphicalDocumentRenderer getRenderer();

  Navigable navigable();

  String getTitle();

  URI getURL();

  void navigate(URI url);

  void reload();

  void back();

  void forward();

  void addEventListener(FrameEventListener listener, boolean sync);

  void addRepaintListener(Runnable repaintListener);

  void removeRepaintListener(Runnable repaintListener);

  void focus();

  void blur();

  default UUID uuid() {
    return navigable().uuid();
  }

  static Frame create(
    RenderingEngine renderingEngine
  ) throws IOException {
    return new FrameImp(renderingEngine);
  }
  
}