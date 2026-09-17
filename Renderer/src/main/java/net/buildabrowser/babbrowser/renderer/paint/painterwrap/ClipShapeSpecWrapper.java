package net.buildabrowser.babbrowser.renderer.paint.painterwrap;

import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;

public class ClipShapeSpecWrapper implements ClipShapeSpec {

  private final ClipShapeSpec innerSpec;
  private final float scalingX;
  private final float scalingY;

  public ClipShapeSpecWrapper(
    ClipShapeSpec innerSpec,
    float scalingX,
    float scalingY
  ) {
    this.innerSpec = innerSpec;
    this.scalingX = scalingX;
    this.scalingY = scalingY;
  }

  @Override
  public ClipShapeSpec addPoint(float x, float y) {
    innerSpec.addPoint(snapX(x), snapY(y));
    return this;
  }

  private float snapX(float x) {
    return Math.round(x * scalingX) / scalingX;
  }

  private float snapY(float y) {
    return Math.round(y * scalingY) / scalingY;
  }

}
