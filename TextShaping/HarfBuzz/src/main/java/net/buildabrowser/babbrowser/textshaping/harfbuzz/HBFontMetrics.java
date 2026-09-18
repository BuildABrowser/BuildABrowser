package net.buildabrowser.babbrowser.textshaping.harfbuzz;

import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_get_glyph;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_get_glyph_extents;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_get_h_extents;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.harfbuzz.hb_font_extents_t;
import org.lwjgl.util.harfbuzz.hb_glyph_extents_t;

import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.TextShaper;

public class HBFontMetrics implements FontMetrics {

  private final TextShaper textShaper;
  private final FontOptions fontOptions;

  private final float ascent;
  private final float descent;
  private final float height;
  private final float xHeight;

  public HBFontMetrics(
    long hbFont,
    TextShaper textShaper,
    FontOptions fontOptions
  ) {
    this.textShaper = textShaper;
    this.fontOptions = fontOptions;

    try (MemoryStack stack = MemoryStack.stackPush()) {
      float size = fontOptions.size();

      hb_font_extents_t extents = hb_font_extents_t.malloc(stack);
      if (hb_font_get_h_extents(hbFont, extents)) {
        this.ascent = -Math.abs(extents.ascender() / 64f);
        this.descent = Math.abs(extents.descender() / 64f);
        
        float lineGap = Math.max(0, extents.line_gap() / 64f);
        this.height = (this.descent - this.ascent) + lineGap;
      } else {
        this.ascent = -size * 0.8f;
        this.descent = size * 0.2f;
        this.height = size;
      }

      IntBuffer glyph = stack.mallocInt(1);
      hb_glyph_extents_t gExtents = hb_glyph_extents_t.malloc(stack);
      this.xHeight =
        hb_font_get_glyph(hbFont, 'x', 0, glyph)
        && hb_font_get_glyph_extents(hbFont, glyph.get(0), gExtents)
        && gExtents.y_bearing() > 0 ?
          gExtents.y_bearing() :
          this.ascent * 0.56f;
    }
  }

  @Override
  public float size() {
    return fontOptions.size();
  }

  @Override
  public int weight() {
    return fontOptions.weight();
  }

  @Override
  public float height() {
    return height;
  }

  @Override
  public float xHeight() {
    return xHeight;
  }

  @Override
  public float ascent() {
    return ascent;
  }

  @Override
  public float descent() {
    return descent;
  }

  @Override
  public float stringWidth(String text) {
    return textShaper.shapeText(text).runSize();
  }
}
