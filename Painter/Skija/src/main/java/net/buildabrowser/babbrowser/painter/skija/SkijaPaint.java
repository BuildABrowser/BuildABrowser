package net.buildabrowser.babbrowser.painter.skija;

import net.buildabrowser.babbrowser.painter.core.BakedPaint;
import net.buildabrowser.babbrowser.painter.core.Paint;

public class SkijaPaint implements Paint {

  private int color;
  private boolean filled = true;
  private float strokeSize = 1f;

  @Override
  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public void setFilled(boolean filled) {
    this.filled = filled;
  }

  @Override
  public void setStrokeSize(float strokeSize) {
    this.strokeSize = strokeSize;
  }

  public BakedPaint bake() {
    return new BakedPaint(color, filled, strokeSize);
  }

}
