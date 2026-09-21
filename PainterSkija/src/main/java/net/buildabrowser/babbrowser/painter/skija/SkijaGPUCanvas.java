package net.buildabrowser.babbrowser.painter.skija;

import java.awt.Graphics;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorSpace;
import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.FramebufferFormat;
import io.github.humbleui.skija.PixelGeometry;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceOrigin;
import io.github.humbleui.skija.SurfaceProps;
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;

public class SkijaGPUCanvas extends AWTGLCanvas {

  private final CanvasCallbacks callbacks;

  private DirectContext context;
  
  private boolean invalid = true;
  private int fboId;
  private Surface surface;

  public SkijaGPUCanvas(CanvasCallbacks callbacks) {
    super(createGLData());
    this.callbacks = callbacks;
  }

  private static GLData createGLData() {
    GLData data = new GLData();
    data.majorVersion = 3;
    data.minorVersion = 3;
    data.profile = GLData.Profile.CORE;
    data.forwardCompatible = true;
    data.stencilSize = 8;
    return data;
  }
  
  @Override
  public void initGL() {
    GL.createCapabilities();

    if (this.context == null) {
      this.context = DirectContext.makeGL();
    }
  }

  @Override
  public void paintGL() {
    int currentFboId = GL31.glGetInteger(GL31.GL_FRAMEBUFFER_BINDING);

    if (
      surface == null
      || surface.getWidth() != getFramebufferWidth()
      || surface.getHeight() != getFramebufferHeight()
      || this.fboId != currentFboId
    ) {
      this.fboId = currentFboId;
      GL11.glViewport(0, 0, getFramebufferWidth(), getFramebufferHeight());
      createSurface();
    }

    if (invalid) {
      callbacks.layout(getWidth(), getHeight());
      invalid = false;
    }

    Canvas rawCanvas = surface.getCanvas();
    rawCanvas.resetMatrix();
    PaintCanvas canvas = new SkijaPaintCanvas(rawCanvas);
    callbacks.paint(canvas);
    context.flushAndSubmit(true);

    this.swapBuffers();
  }

  @Override
  public void invalidate() {
    this.invalid = true;
    super.invalidate();
  }

  @Override
  public void paint(Graphics g) {
    this.render();
  }

  @Override
  public void removeNotify() {
    if (this.context != null && !context.isClosed()) {
      context.abandon();
    }
    super.removeNotify();
  }

  private void createSurface() {
    BackendRenderTarget renderTarget = BackendRenderTarget.makeGL(
      getFramebufferWidth(), getFramebufferHeight(),
      0, 8, fboId,
      FramebufferFormat.GR_GL_RGBA8);

    this.surface = Surface.wrapBackendRenderTarget(
      context,
      renderTarget,
      SurfaceOrigin.BOTTOM_LEFT,
      ColorType.RGBA_8888,
      ColorSpace.getSRGB(),
      new SurfaceProps(PixelGeometry.RGB_H));
  }

}
