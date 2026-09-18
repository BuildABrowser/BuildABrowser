package net.buildabrowser.babbrowser.painter.skija;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Matrix44;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.skija.TextLine;
import io.github.humbleui.types.Rect;
import net.buildabrowser.babbrowser.painter.core.BakedPaint;
import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Transform;
import net.buildabrowser.babbrowser.textshaping.core.ShapedText;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;

public class SkijaPaintCanvas implements PaintCanvas {
 
  private final Deque<Matrix44> matrixStack = new ArrayDeque<>();

  private final io.github.humbleui.skija.Paint rawPaint = new io.github.humbleui.skija.Paint();
  private final Canvas canvas;
  private final SkijaTransform transform;

  private BakedPaint currentPaint = new BakedPaint(
    0xFFFFFFFF, true, 1f);

  public SkijaPaintCanvas(Canvas canvas) {
    this.canvas = canvas;
    this.transform = new SkijaTransform(canvas);
    syncPaint(currentPaint);
  }

  @Override
  public void withPaint(
    Consumer<Paint> alterPaintFunc, Consumer<PaintCanvas> paintFunc
  ) {
    BakedPaint oldPaint = currentPaint;
    
    // TODO: Re-use paint instances
    SkijaPaint paint = new SkijaPaint();
    paint.setColor(oldPaint.color());
    alterPaintFunc.accept(paint);
    this.currentPaint = paint.bake();
    syncPaint(currentPaint);

    paintFunc.accept(this);

    this.currentPaint = oldPaint;
    syncPaint(currentPaint);
  }

  @Override
  public void withTransform(Consumer<Transform> alterTransformFunc, Consumer<PaintCanvas> paintFunc) {
    canvas.save();
    alterTransformFunc.accept(transform);
    paintFunc.accept(this);
    canvas.restore();
  }

  @Override
  public void saveTransform(Consumer<PaintCanvas> paintFunc) {
    matrixStack.push(canvas.getLocalToDevice());
    paintFunc.accept(this);
    canvas.setMatrix(matrixStack.pop());
  }

  @Override
  public void restoreTransform(Consumer<PaintCanvas> paintFunc) {
    Matrix44 oldMatrix = canvas.getLocalToDevice();
    canvas.setMatrix(matrixStack.peek());
    paintFunc.accept(this);
    canvas.setMatrix(oldMatrix);
  }

  @Override
  public void withClip(float x, float y, float w, float h, Consumer<PaintCanvas> paintFunc) {
    canvas.save();
    canvas.clipRect(Rect.makeXYWH(x, y, w, h));
    paintFunc.accept(this);
    canvas.restore();
  }

  @Override
  public void withShapedClip(Consumer<ClipShapeSpec> shapeFunc, Consumer<PaintCanvas> paintFunc) {
    canvas.save();
    SkijaClipShapeSpec spec = new SkijaClipShapeSpec();
    shapeFunc.accept(spec);
    canvas.clipPath(spec.path());
    paintFunc.accept(this);
    canvas.restore();
  }

  @Override
  public void drawBox(float x, float y, float w, float h) {
    canvas.drawRect(Rect.makeXYWH(x, y, w, h), rawPaint);
  }

  @Override
  public void drawCircle(float x, float y, float r) {
    canvas.drawCircle(x + r, y + r, r, rawPaint);
  }

  @Override
  public void drawText(float x, float y, TextRun textRuns) {
    TextRun nextRun = textRuns;
    while (nextRun != null) {
      TextRun textRun = nextRun;
      nextRun = nextRun.next();

      ShapedText shapedText = textRun.shapedText();
      Font font = ((SkijaFontIdentifier) shapedText.fontIdentifier()).font();
      float textX = x + textRun.offsetX();
      float textY = y + textRun.offsetY();
      if (textRun.shapedText().glyphs() == null) {
        TextLine textLine = TextLine.make(
          shapedText.fallbackText(), font);
        canvas.drawTextLine(textLine, textX, textY, rawPaint);
      } else {
        if (shapedText.glyphs().length == 0) continue;
        // TODO: Use y position array
        float posY = shapedText.positionsY()[0];
        TextBlob textBlob = TextBlob.makeFromPosH(
          shapedText.glyphs(), shapedText.positionsX(), posY, font);
        canvas.drawTextBlob(textBlob, textX, textY, rawPaint);
      }
    }
  }

  @Override
  public void drawImage(float x, float y, LoadedImage image) {
    drawImage(x, y, image.width(), image.height(), image);
  }

  @Override
  public void drawImage(float x, float y, float w, float h, LoadedImage image) {
    Rect rect = Rect.makeXYWH(x, y, w, h);
    canvas.drawImageRect(((SkijaLoadedImage) image).image(), rect, rawPaint);
  }

  @Override
  public void drawBitMap(int x, int y, PaintBitMap bitMap) {
    ((SkijaPaintBitMap) bitMap).draw(canvas, null, x, y);
  }

  private void syncPaint(BakedPaint paint) {
    rawPaint.setColor(paint.color());
    rawPaint.setStroke(!paint.filled());
    rawPaint.setStrokeWidth(paint.strokeSize());
  }
  
}
