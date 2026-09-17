package net.buildabrowser.babbrowser.renderer.paint.painterwrap;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Transform;

public class PaintCanvasWrapper implements PaintCanvas {

  private final PaintCanvas innerCanvas;

  private float scalingX;
  private float scalingY;

  public PaintCanvasWrapper(
    PaintCanvas innerCanvas,
    float scalingX,
    float scalingY
  ) {
    this.innerCanvas = innerCanvas;
    this.scalingX = scalingX;
    this.scalingY = scalingY;
  }

  @Override
  public void withPaint(Consumer<Paint> alterPaintFunc, Consumer<PaintCanvas> paintFunc) {
    innerCanvas.withPaint(alterPaintFunc, _1 -> paintFunc.accept(this));
  }

  @Override
  public void withTransform(Consumer<Transform> alterTransformFunc, Consumer<PaintCanvas> paintFunc) {
    float oldScalingX = this.scalingX;
    float oldScalingY = this.scalingY;
    innerCanvas.withTransform(
      t -> {
        TransformWrapper t2 = new TransformWrapper(t, scalingX, scalingY);
        alterTransformFunc.accept(t2);
        this.scalingX = t2.scalingX();
        this.scalingY = t2.scalingY();
      },
      _1 -> paintFunc.accept(this));
    this.scalingX = oldScalingX;
    this.scalingY = oldScalingY;
  }

  @Override
  public void saveTransform(Consumer<PaintCanvas> paintFunc) {
    innerCanvas.saveTransform(_1 -> paintFunc.accept(this));
  }

  @Override
  public void restoreTransform(Consumer<PaintCanvas> paintFunc) {
    // TODO: Need to restore scaling (preferably without using a stack)
    innerCanvas.restoreTransform(_1 -> paintFunc.accept(this));
  }

  @Override
  public void withClip(float x, float y, float w, float h, Consumer<PaintCanvas> paintFunc) {
    innerCanvas.withClip(
      snapX(x), snapY(y), snapW(x, w), snapH(y, h),
      _1 -> paintFunc.accept(this));
  }

  @Override
  public void withShapedClip(Consumer<ClipShapeSpec> shapeFunc, Consumer<PaintCanvas> paintFunc) {
    innerCanvas.withShapedClip(
      c -> shapeFunc.accept(new ClipShapeSpecWrapper(c, scalingX, scalingY)),
      _1 -> paintFunc.accept(this));
  }

  @Override
  public void drawBox(float x, float y, float w, float h) {
    innerCanvas.drawBox(snapX(x), snapY(y), snapW(x, w), snapH(y, h));
  }

  @Override
  public void drawCircle(float x, float y, float r) {
    innerCanvas.drawCircle(snapX(x), snapY(y), snapX(r));
  }

  @Override
  public void drawText(float x, float y, String text) {
    innerCanvas.drawText(snapX(x), snapY(y), text);
  }

  @Override
  public void drawImage(float x, float y, LoadedImage image) {
    this.drawImage(x, y, image.width(), image.height(), image);
  }

  @Override
  public void drawImage(float x, float y, float w, float h, LoadedImage image) {
    innerCanvas.drawImage(snapX(x), snapY(y), snapW(x, w), snapH(y, h), image);
  }

  @Override
  public void drawBitMap(int x, int y, PaintBitMap bitMap) {
    innerCanvas.drawBitMap(x, y, bitMap);
  }

  @Override
  public FontMetrics fontMetrics() {
    return innerCanvas.fontMetrics();
  }

  private float snapX(float x) {
    return Math.round(x * scalingX) / scalingX;
  }

  private float snapW(float x, float w) {
    return snapX(x + w) - snapX(x);
  }

  private float snapY(float y) {
    return Math.round(y * scalingY) / scalingY;
  }

  private float snapH(float y, float h) {
    return snapY(y + h) - snapY(y);
  }
  
}
