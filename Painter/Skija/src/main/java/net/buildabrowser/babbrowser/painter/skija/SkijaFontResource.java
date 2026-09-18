package net.buildabrowser.babbrowser.painter.skija;

import java.nio.ByteBuffer;

import io.github.humbleui.skija.StreamAsset;
import io.github.humbleui.skija.Typeface;
import net.buildabrowser.babbrowser.textshaping.core.FontIdentifier;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;

public class SkijaFontResource implements FontResource {

  private final Typeface typeface;
  private final FontIdentifier fontIdentifier;

  public SkijaFontResource(
    Typeface typeface,
    FontIdentifier fontIdentifier
  ) {
    this.typeface = typeface;
    this.fontIdentifier = fontIdentifier;
  }

  @Override
  public FontIdentifier fontIdentifier() {
    return this.fontIdentifier;
  }

  @Override
  public ByteBuffer syncBuffer() {
    try (StreamAsset fontData = typeface.openStream()) {
      int length = (int) fontData.getLength();
      byte[] fontBytes = new byte[length];
      fontData.read(fontBytes, length);

      ByteBuffer buffer = ByteBuffer.allocateDirect(length);
      buffer.put(fontBytes).flip();

      return buffer;
    }
  }

}
