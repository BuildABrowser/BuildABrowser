package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.painter.core.BakedPaint;
import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Transform;
import net.buildabrowser.babbrowser.textshaping.core.ShapedText;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;

// TODO: Find out why page rendering seems to be missing some elements present on the Skija painter
public class J2DPaintCanvas implements PaintCanvas {

  private final Deque<AffineTransform> transformStack = new ArrayDeque<>();

  private final J2DTransform transform;

  private Graphics2D graphics;
  private BakedPaint currentPaint = new BakedPaint(
    0xFFFFFFFF, true, 1f);

  public J2DPaintCanvas(Graphics2D graphics) {
    this.graphics = graphics;
    this.transform = new J2DTransform(graphics);
  }

  @Override
  public void withPaint(
    Consumer<Paint> alterPaintFunc, Consumer<PaintCanvas> paintFunc
  ) {
    BakedPaint oldPaint = currentPaint;
    
    // TODO: Re-use paint instances
    J2DPaint paint = new J2DPaint();
    paint.setColor(oldPaint.color());
    alterPaintFunc.accept(paint);
    this.currentPaint = paint.bake();
    syncPaint(currentPaint);

    paintFunc.accept(this);

    this.currentPaint = oldPaint;
    syncPaint(currentPaint);
  }

  @Override
  public void withTransform(
    Consumer<Transform> alterTransformFunc, Consumer<PaintCanvas> paintFunc
  ) {
    AffineTransform oldTransform = graphics.getTransform();
    AffineTransform newTransform = (AffineTransform) oldTransform.clone();
    graphics.setTransform(newTransform);
    alterTransformFunc.accept(transform);
    paintFunc.accept(this);
    graphics.setTransform(oldTransform);
  }

  @Override
  public void saveTransform(Consumer<PaintCanvas> paintFunc) {
    transformStack.push((AffineTransform) graphics.getTransform().clone());
    paintFunc.accept(this);
    graphics.setTransform(transformStack.pop());
  }

  @Override
  public void restoreTransform(Consumer<PaintCanvas> paintFunc) {
    AffineTransform oldTransform = graphics.getTransform();
    graphics.setTransform(transformStack.peek());
    paintFunc.accept(this);
    graphics.setTransform(oldTransform);
  }

  @Override
  public void withClip(
    float x, float y, float w, float h, Consumer<PaintCanvas> paintFunc
  ) {
    Shape oldClip = graphics.getClip();
    graphics.clipRect((int) x, (int) y, (int) w, (int) h);
    paintFunc.accept(this);
    graphics.setClip(oldClip);
  }

  @Override
  public void withShapedClip(Consumer<ClipShapeSpec> shapeFunc, Consumer<PaintCanvas> paintFunc) {
    Shape oldClip = graphics.getClip();
    J2DClipShapeSpec spec = new J2DClipShapeSpec();
    shapeFunc.accept(spec);
    Area clipArea = new Area(spec.shape());
    if (oldClip != null) {
      clipArea.intersect(new Area(oldClip));
    }
    graphics.setClip(clipArea);
    paintFunc.accept(this);
    graphics.setClip(oldClip);
  }

  @Override
  public void drawBox(float x, float y, float w, float h) {
    if (currentPaint.filled()) {
      graphics.fillRect((int) x, (int) y, (int) w, (int) h);
    } else {
      graphics.drawRect((int) x, (int) y, (int) w, (int) h);
    }
  }

  @Override
  public void drawCircle(float x, float y, float r) {
    int diameter = (int) (r * 2);
    if (currentPaint.filled()) {
      graphics.fillOval((int) x, (int) y, diameter, diameter);
    } else {
      graphics.drawOval((int) x, (int) y, diameter, diameter);
    }
  }

  @Override
  public void drawText(float x, float y, TextRun textRuns) {
    FontRenderContext renderContext = graphics.getFontRenderContext();

    TextRun nextRun = textRuns;
    while (nextRun != null) {
      TextRun textRun = nextRun;
      nextRun = nextRun.next();

      ShapedText shapedText = textRun.shapedText();
      Font font = ((J2DFontIdentifier) shapedText.fontIdentifier()).font();
      float textX = x + textRun.offsetX();
      float textY = y + textRun.offsetY();

      if (shapedText.glyphs() == null) {
        graphics.setFont(font);
        graphics.drawString(shapedText.fallbackText(), textX, textY);
      } else {
        short[] glyphs = shapedText.glyphs();
        if (glyphs.length == 0) continue;

        int[] glyphCodes = new int[glyphs.length];
        for (int i = 0; i < glyphs.length; i++) {
          glyphCodes[i] = glyphs[i] & 0xFFFF;
        }

        GlyphVector glyphVector = font.createGlyphVector(renderContext, glyphCodes);

        float[] posX = shapedText.positionsX();
        float[] posY = shapedText.positionsY();

        for (int i = 0; i < glyphs.length; i++) {
          glyphVector.setGlyphPosition(i, new Point2D.Float(posX[i], posY[i]));
        }

        graphics.drawGlyphVector(glyphVector, textX, textY);
      }
    }
  }

  @Override
  public void drawImage(float x, float y, LoadedImage image) {
    graphics.drawImage(((J2DLoadedImage) image).image(), (int) x, (int) y, (int) image.width(), (int) image.height(), null);
  }

  @Override
  public void drawImage(float x, float y, float w, float h, LoadedImage image) {
    graphics.drawImage(((J2DLoadedImage) image).image(), (int) x, (int) y, (int) w, (int) h, null);
  }

  @Override
  public void drawBitMap(int x, int y, PaintBitMap bitMap) {
    BufferedImage image = ((J2DBitMap) bitMap).image();
    graphics.drawImage(image, x, y, (int) image.getWidth(), image.getHeight(), null);
  }

  private void syncPaint(BakedPaint paint) {
    graphics.setColor(new Color(paint.color(), true));
    graphics.setBackground(new Color(paint.color(), true));
    graphics.setStroke(new BasicStroke(paint.strokeSize()));
  }
  
}
