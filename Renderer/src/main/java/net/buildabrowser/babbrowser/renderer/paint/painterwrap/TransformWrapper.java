package net.buildabrowser.babbrowser.renderer.paint.painterwrap;

import net.buildabrowser.babbrowser.painter.core.Transform;

public class TransformWrapper implements Transform {

  private final Transform innerTransform;

  private float scalingX = 1f;
  private float scalingY = 1f;

  public TransformWrapper(
    Transform innerTransform,
    float scalingX,
    float scalingY
  ) {
    this.innerTransform = innerTransform;
    this.scalingX = scalingX;
    this.scalingY = scalingY;
  }

  // TODO: Internally accumulate the transform for better snapping decisions
  @Override
  public void translate(float x, float y) {
    innerTransform.translate(snapX(x), snapY(y));
  }

  @Override
  public void scale(float scalingX, float scalingY) {
    this.scalingX *= scalingX;
    this.scalingY *= scalingY;
    innerTransform.scale(scalingX, scalingY);
  }

  public float scalingX() {
    return this.scalingX;
  }

  public float scalingY() {
    return this.scalingY;
  }

  private float snapX(float value) {
    return Math.round(value * scalingX) / scalingX;
  }

  private float snapY(float value) {
    return Math.round(value * scalingY) / scalingY;
  }

}
