package net.buildabrowser.babbrowser.renderer.imp;

import java.io.IOException;
import java.util.Optional;

import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.RendererTransformOptions;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.uistate.FrameAPIs;

public class DelegatingGraphicalDocumentRenderer implements GraphicalDocumentRenderer {

  private static final GraphicalDocumentRenderer NO_OP_RENDERER = new NoOpGraphicalDocumentRenderer();

  private final Navigable navigable;

  public DelegatingGraphicalDocumentRenderer(Navigable navigable) {
    this.navigable = navigable;
  }

  @Override
  public boolean shouldRender() {
    return activeRenderer().shouldRender();
  }

  @Override
  public void recalculateStyles() {
    activeRenderer().recalculateStyles();
  }

  @Override
  public void updateLayout() {
    activeRenderer().updateLayout();
  }

  @Override
  public void updateRendering() {
    activeRenderer().updateRendering();
  }

  @Override
  public void resize(
    int width, int height,
    RendererTransformOptions transformOptions
  ) {
    activeRenderer().resize(width, height, transformOptions);
  }

  @Override
  public void draw(
    PaintCanvas context,
    RendererTransformOptions transformOptions
  ) {
    activeRenderer().draw(context, transformOptions);
  }

  @Override
  public DocumentChangeListener changeListener() {
    return activeRenderer().changeListener();
  }

  @Override
  public void onDocumentInvalidated(short invalidationLevel) {
    activeRenderer().onDocumentInvalidated(invalidationLevel);
  }

  @Override
  public void onDocumentFocused() {
    activeRenderer().onDocumentFocused();
  }

  @Override
  public void onDocumentBlurred() {
    activeRenderer().onDocumentBlurred();
  }

  @Override
  public Optional<String> title() {
    return activeRenderer().title();
  }

  @Override
  public EventForwardingTarget eventForwardingTarget() {
    return activeRenderer().eventForwardingTarget();
  }

  @Override
  public void close() throws IOException {
    activeRenderer().close();
  }
  
  public FrameAPIs frameAPIs() {
    return activeRenderer().frameAPIs();
  }

  private GraphicalDocumentRenderer activeRenderer() {
    GraphicalDocumentRenderer activeRenderer = (GraphicalDocumentRenderer) navigable.activeDocument().renderer();
    return activeRenderer == null ?
      NO_OP_RENDERER :
      activeRenderer;
  }
  
}
