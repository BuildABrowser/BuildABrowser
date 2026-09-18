package net.buildabrowser.babbrowser.textshaping.harfbuzz;

import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_destroy;

import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.harfbuzz.HarfBuzz;

import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.OneLoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextShaper;

public class HBOneLoadedFont implements OneLoadedFont {

  private final long hbFont;
  private final TextShaper textShaper;
  private final FontMetrics metrics;

  public HBOneLoadedFont(
    long hbFont,
    TextShaper textShaper,
    FontMetrics metrics
  ) {
    this.hbFont = hbFont;
    this.textShaper = textShaper;
    this.metrics = metrics;
  }

  @Override
  public FontMetrics metrics() {
    return this.metrics;
  }

  @Override
  public TextShaper shaper() {
    return this.textShaper;
  }

  @Override
  public short getUTF32Glyph(int codePoint) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer glyph = stack.mallocInt(1);
      boolean hasGlyph = HarfBuzz.hb_font_get_glyph(hbFont, codePoint, 0, glyph);
      if (hasGlyph) {
        return (short) glyph.get(0);
      }
      return 0;
    }
  }

  @Override
  public void close() throws IOException {
    hb_font_destroy(hbFont);
  }

}
