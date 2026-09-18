package net.buildabrowser.babbrowser.renderer.paint;

import java.util.function.Consumer;

public class VpIntersection {
  
  private final int vpW, vpH;
  private float bufferVpX, bufferVpY;
  private float bufferX, bufferY;
  private int bufferW, bufferH;

  public VpIntersection(float vpW, float vpH) {
    this.vpW = (int) Math.ceil(vpW);
    this.vpH = (int) Math.ceil(vpH);
    this.bufferW = this.vpW;
    this.bufferH = this.vpH;
  }

  public int vpWidth() {
    return this.vpW;
  }

  public int vpHeight() {
    return this.vpH;
  }

  public float bufferVpX() {
    return this.bufferVpX;
  }

  public float bufferVpY() {
    return this.bufferVpY;
  }

  public float bufferX() {
    return this.bufferX;
  }

  public float bufferY() {
    return this.bufferY;
  }

  public int bufferWidth() {
    return this.bufferW;
  }

  public int bufferHeight() {
    return this.bufferH;
  }

  public void enterLayer(
    float x, float y,
    Consumer<VpIntersection> elFunc
  ) {
    float oldBufferVpX = bufferVpX, oldBufferVpY = bufferVpY;
    bufferVpX += x;
    bufferVpY += y;
    elFunc.accept(this);
    bufferVpX = oldBufferVpX; bufferVpY = oldBufferVpY;
  }

  public void enterBuffer(
    float x, float y, float w, float h,
    Consumer<VpIntersection> elFunc
  ) {
    float oldBufferX = bufferX, oldBufferY = bufferY;
    int oldBufferW = bufferW, oldBufferH = bufferH;
    // TODO: More precise rounding
    bufferX = x;
    bufferY = y;
    bufferW = (int) Math.ceil(w);
    bufferH = (int) Math.ceil(h);
    elFunc.accept(this);
    
    bufferX = oldBufferX; bufferY = oldBufferY;
    bufferW = oldBufferW; bufferH = oldBufferH;
  }

}
