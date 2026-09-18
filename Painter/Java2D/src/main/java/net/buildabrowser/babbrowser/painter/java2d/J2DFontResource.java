package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Font;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.buildabrowser.babbrowser.textshaping.core.FontIdentifier;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;

public class J2DFontResource implements FontResource {

  private final Font font;
  private final FontIdentifier fontIdentifier;

  public J2DFontResource(
    Font font,
    FontIdentifier fontIdentifier
  ) {
    this.font = font;
    this.fontIdentifier = fontIdentifier;
  }

  @Override
  public FontIdentifier fontIdentifier() {
    return this.fontIdentifier;
  }

  @Override
  public ByteBuffer syncBuffer() {
    String filePath = resolveFontFilePath(font);
    if (filePath == null) {
      throw new IllegalStateException("Could not resolve backing font file for: " + font);
    }

    try (
      RandomAccessFile file = new RandomAccessFile(new File(filePath), "r");
      FileChannel channel = file.getChannel()
    ) {
      
      ByteBuffer buffer = ByteBuffer.allocateDirect((int) channel.size());
      channel.read(buffer);
      buffer.flip();
      return buffer;
    } catch (Exception e) {
      throw new RuntimeException("Failed to read font file into buffer: " + filePath, e);
    }
  }

  private String resolveFontFilePath(java.awt.Font font) {
    try {
      Class<?> fontUtilities = Class.forName("sun.font.FontUtilities");
      Method getFont2D = fontUtilities.getDeclaredMethod("getFont2D", java.awt.Font.class);
      Object font2D = getFont2D.invoke(null, font);

      if (font2D == null) return null;

      if (font2D.getClass().getSimpleName().equals("CompositeFont")) {
        Method getSlotFont = font2D.getClass().getMethod("getSlotFont", int.class);
        font2D = getSlotFont.invoke(font2D, 0);
      }

      Class<?> current = font2D.getClass();
      while (current != null) {
        try {
          Field platNameField = current.getDeclaredField("platName");
          platNameField.setAccessible(true);
          return (String) platNameField.get(font2D);
        } catch (NoSuchFieldException e) {
          current = current.getSuperclass();
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to resolve font path via sun.font", e);
    }
    return null;
  }

}
