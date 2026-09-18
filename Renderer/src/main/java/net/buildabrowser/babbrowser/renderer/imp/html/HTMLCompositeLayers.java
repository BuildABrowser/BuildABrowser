package net.buildabrowser.babbrowser.renderer.imp.html;

import java.util.function.Consumer;
import java.util.function.Function;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.RendererTransformOptions;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayer;
import net.buildabrowser.babbrowser.renderer.composite.imp.CompositeLayerGeneratorImp;
import net.buildabrowser.babbrowser.renderer.layout.stacking.LayerGenerator;
import net.buildabrowser.babbrowser.renderer.layout.stacking.LayerGeneratorContext;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painterwrap.PaintCanvasWrapper;

public class HTMLCompositeLayers {
  
  private final Object frontLayerLock = new Object();

  private final LayerGenerator<CompositeLayer> layerGenerator;

  private StackingContext frontLayerRegenContext;
  private CompositeLayer rootLayerBack;
  private CompositeLayer rootLayerFront;

  public HTMLCompositeLayers(Painter painter) {
    this.layerGenerator = new CompositeLayerGeneratorImp(painter);
  }

  public void regenerate(
    StackingContext stackingContext,
    RendererTransformOptions transformOptions
  ) {
    LayerGeneratorContext context = new LayerGeneratorContext(
      transformOptions.vpScaleX(), transformOptions.vpScaleY());
    this.rootLayerBack = stackingContext.createLayer(
      layerGenerator, context);
    this.frontLayerRegenContext = stackingContext;
  }

  public void updateRendering(
    int width, int height,
    RendererTransformOptions transformOptions
  ) {
    if (rootLayerBack == null) return;

    VpIntersection vpIntersection = new VpIntersection(width, height);
    rootLayerBack.repaint(vpIntersection);

    synchronized (frontLayerLock) {
      CompositeLayer oldFrontLayer = rootLayerFront;
      rootLayerFront = rootLayerBack;
      rootLayerBack = oldFrontLayer;
    }

    if (frontLayerRegenContext != null) {
      LayerGeneratorContext context = new LayerGeneratorContext(
        transformOptions.vpScaleX(), transformOptions.vpScaleY());
      this.rootLayerBack = frontLayerRegenContext.createLayer(layerGenerator, context);
      this.frontLayerRegenContext = null;
    }
  }

  public void draw(
    PaintCanvas canvas,
    int width, int height,
    RendererTransformOptions transformOptions
  ) {
    canvas = new PaintCanvasWrapper(canvas, 1f, 1f);

    synchronized (frontLayerLock) {
      if (
        this.rootLayerFront == null
        || width <= 0
        || height <= 0
      ) return;

      canvas.withPaint(
        p -> p.setColor(0xFFFFFFFF),
        c -> c.drawBox(0, 0, width, height));
      // TODO: What if the root layer is updating internally while painting? (sync)
      VpIntersection vpIntersection = new VpIntersection(
        width / transformOptions.vpScaleX(),
        height / transformOptions.vpScaleY());
      canvas.withTransform(
        t -> t.scale(transformOptions.vpScaleX(), transformOptions.vpScaleY()),
        c1 -> c1.saveTransform(
          c2 -> rootLayerFront.draw(c2, vpIntersection)));
    }
  }

  public void withFrontLayerV(Consumer<CompositeLayer> layerFunc) {
    synchronized (frontLayerLock) {
      if (rootLayerFront == null) return;
      layerFunc.accept(rootLayerFront);
    }
  }

  public <T> T withFrontLayer(Function<CompositeLayer, T> layerFunc) {
    synchronized (frontLayerLock) {
      if (rootLayerFront == null) return null;
      return layerFunc.apply(rootLayerFront);
    }
  }

}
