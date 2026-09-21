package net.buildabrowser.babbrowser.embedding.swing;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;

import net.buildabrowser.babbrowser.embedding.standardcommon.StandardCommonEmbedding;
import net.buildabrowser.babbrowser.embedding.swing.clipboard.AWTClipboardProvider;
import net.buildabrowser.babbrowser.embedding.swing.input.RendererKeyboardInputAdapter;
import net.buildabrowser.babbrowser.embedding.swing.input.RendererMouseInputAdapter;
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.java2d.Java2DPainter;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.RendererTransformOptions;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.RenderingEngineBuilder;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.imp.NoOpGraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public final class SwingEmbedding {

  private static final GraphicalDocumentRenderer NO_OP_RENDERER = new NoOpGraphicalDocumentRenderer();
  
  private SwingEmbedding() {}

  public static void configure(RenderingEngineBuilder builder) {
    StandardCommonEmbedding.configure(builder);
    ClipboardProvider<?> clipboardProvider = new AWTClipboardProvider();
    builder
      .setClipboardProvider(clipboardProvider)
      .setPainter(new Java2DPainter());
  }

  public static FrameAndComponent newFrameComponent(
    String url
  ) throws IOException {
    return newFrameComponent(url, null);
  }

  public static FrameAndComponent newFrameComponent(
    String url, ComponentPainter<Component> painter
  ) throws IOException {
    RenderingEngineBuilder builder = RenderingEngineBuilder.create();
    SwingEmbedding.configure(builder);
    if (painter != null) {
      builder.setPainter(painter);
    }
    RenderingEngine renderingEngine = builder.build();

    Frame frame = renderingEngine.createFrame();
    Component frameComponent = SwingEmbedding.createFrameComponent(frame);
    frame.navigate(URI.create(url));
    return new FrameAndComponent(frame, frameComponent);
  }

  @SuppressWarnings("unchecked")
  public static Component createFrameComponent(Frame frame) {
    return createFrameComponent(
      (ComponentPainter<Component>) frame.renderingEngine().painter(),
      () -> frame);
  }

  public static Component createFrameComponent(
    ComponentPainter<Component> painter,
    Supplier<Frame> activeFrameSupplier
  ) {
    RendererTransformOptions transformOptions = new RendererTransformOptions(1f, 1f);
    AtomicReference<Component> currentComponent = new AtomicReference<>();
    Runnable repaintListener = () -> SwingUtilities.invokeLater(
      () -> notifyActivateFrame(currentComponent.get()));

    Component panel = painter.createComponent(new CanvasCallbacks() {

      private WeakReference<Frame> lastFrameRef = null;

      @Override
      public void layout(float width, float height) {
        GraphicalDocumentRenderer activeRenderer = activateFrame(activeFrameSupplier);

        if (activeRenderer == null) return;
        // TODO: Make renderer accept float instead?
        activeRenderer.resize(
          (int) Math.ceil(width), (int) Math.ceil(height),
          transformOptions);
      }

      @Override
      public void paint(PaintCanvas canvas) {
        GraphicalDocumentRenderer activeRenderer = activateFrame(activeFrameSupplier);
        if (activeRenderer == null) return;
        activeRenderer.draw(canvas, transformOptions);
      }

      private GraphicalDocumentRenderer activateFrame(Supplier<Frame> activeFrameSupplier) {
        Frame activeFrame = activeFrameSupplier.get();
        Frame lastFrame = lastFrameRef == null ? null : lastFrameRef.get();
        if (activeFrame == null) return null;
        if (activeFrame == lastFrame) {
          return activeRenderer(() -> activeFrame);
        }

        if (lastFrame != null) {
          lastFrame.removeRepaintListener(repaintListener);
          lastFrame.blur();
        }
        activeFrame.addRepaintListener(repaintListener);
        if (currentComponent.get().hasFocus()) {
          activeFrame.focus();
        }
        this.lastFrameRef = new WeakReference<>(activeFrame);
        return activeRenderer(() -> activeFrame);
      }

      // TODO: Handle invalidation listener
      
    });
    currentComponent.set(panel);

    configureFrameComponent(activeFrameSupplier, panel);

    return panel;
  }

  public static void notifyActivateFrame(Component component) {
    if (component == null) return;
    SwingUtilities.invokeLater(() -> {
      component.revalidate();
      component.repaint();
    });
  }

  private static void configureFrameComponent(Supplier<Frame> activeFrameSupplier, Component panel) {
    // TODO: Still allow focus to loop at start/end of document
    panel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet());
    panel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
    panel.setFocusable(true);

    RendererMouseInputAdapter mouseHandler = new RendererMouseInputAdapter(panel,
      () -> activeRenderer(activeFrameSupplier));
    panel.addMouseListener(mouseHandler);
    panel.addMouseMotionListener(mouseHandler);
    panel.addMouseWheelListener(mouseHandler);

    RendererKeyboardInputAdapter keyboardHandler = new RendererKeyboardInputAdapter(
      () -> activeRenderer(activeFrameSupplier));
    panel.addKeyListener(keyboardHandler);

    panel.addFocusListener(new FocusListener() {

      @Override
      public void focusGained(FocusEvent e) {
        Frame frame = activeFrameSupplier.get();
        if (frame == null) return;
        frame.focus();
      }

      @Override
      public void focusLost(FocusEvent e) {
        Frame frame = activeFrameSupplier.get();
        if (frame == null) return;
        frame.blur();
      }
      
    });
  }

  private static GraphicalDocumentRenderer activeRenderer(Supplier<Frame> activeFrameSupplier) {
    Frame frame = activeFrameSupplier.get();
    if (frame == null) {
      return NO_OP_RENDERER;
    }

    return frame.getRenderer();
  }

  public static record FrameAndComponent(
    Frame frame,
    Component component
  ) {
  }

}
