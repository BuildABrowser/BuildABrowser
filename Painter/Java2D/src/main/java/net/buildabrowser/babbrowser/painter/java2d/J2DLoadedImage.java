package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.common.datastruct.IOThrowingConsumer;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;

public record J2DLoadedImage(BufferedImage image) implements LoadedImage {

  @Override
  public int width() {
    return image.getWidth();
  }

  @Override
  public int height() {
    return image.getHeight();
  }

  @Override
  public void streamData(IOThrowingConsumer<InputStream> dataReader) throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
        
      InputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
      dataReader.accept(stream);
    }
  }
  
}
