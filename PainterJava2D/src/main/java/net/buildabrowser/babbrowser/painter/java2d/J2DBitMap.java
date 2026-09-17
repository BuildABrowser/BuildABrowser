package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;

public class J2DBitMap implements PaintBitMap {

  private final BufferedImage bitMapImage;

  public J2DBitMap(BufferedImage bitMapImage) {
    this.bitMapImage = bitMapImage;
  }

  @Override
  public void withCanvas(Consumer<PaintCanvas> paintFunc) {
    Graphics2D graphics = bitMapImage.createGraphics();
    graphics.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );
    graphics.setRenderingHint(
      RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR
    );

    graphics.setBackground(new Color(0, 0, 0, 0));
    graphics.clearRect(0, 0, bitMapImage.getWidth(), bitMapImage.getHeight());
    J2DPaintCanvas canvas = new J2DPaintCanvas(graphics);
    paintFunc.accept(canvas);
  }

  public BufferedImage image() {
    return this.bitMapImage;
  }

}
