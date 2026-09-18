package net.buildabrowser.babbrowser.textshaping.harfbuzz;

import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_add_utf8;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_destroy;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_get_glyph_infos;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_get_glyph_positions;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_guess_segment_properties;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_shape;

import org.lwjgl.util.harfbuzz.hb_glyph_info_t;
import org.lwjgl.util.harfbuzz.hb_glyph_position_t;

import net.buildabrowser.babbrowser.textshaping.core.FontIdentifier;
import net.buildabrowser.babbrowser.textshaping.core.ShapedText;
import net.buildabrowser.babbrowser.textshaping.core.TextShaper;

public class HBTextShaper implements TextShaper {

  private final long currentFont;
  private final FontIdentifier fontIdentifier;

  public HBTextShaper(
    long currentFont,
    FontIdentifier fontIdentifier
  ) {
    this.currentFont = currentFont;
    this.fontIdentifier = fontIdentifier;
  }

  // Skija's built in shaping mechanisms are too slow, so use manual shaping instead
  // TODO: Need to support bidi and stuff
  @Override 
  public ShapedText shapeText(String text) {
    if (text.isEmpty()) {
      return new ShapedText(
        new short[0], new float[0], new float[0],
        0f, text, fontIdentifier);
    }

    long hbBuffer = hb_buffer_create();
    try {
      hb_buffer_add_utf8(hbBuffer, text, 0, -1);
      hb_buffer_guess_segment_properties(hbBuffer);
      hb_shape(currentFont, hbBuffer, null);
      
      hb_glyph_info_t.Buffer glyphInfos = hb_buffer_get_glyph_infos(hbBuffer);
      hb_glyph_position_t.Buffer glyphPositions = hb_buffer_get_glyph_positions(hbBuffer);
      
      int glyphCount = glyphInfos.limit();

      short[] glyphs = new short[glyphCount];
      float[] positionsX = new float[glyphCount];
      float[] positionsY = new float[glyphCount];
      int runningPositionX = 0;
      int runningPositionY = 0;
      for (int i = 0; i < glyphCount; i++) {
        glyphs[i] = (short) glyphInfos.get(i).codepoint();

        positionsX[i] = (runningPositionX + glyphPositions.get(i).x_offset()) / 64f;
        positionsY[i] = (runningPositionY + glyphPositions.get(i).y_offset()) / 64f;
        runningPositionX += glyphPositions.get(i).x_advance();
        runningPositionY += glyphPositions.get(i).y_advance();
      }


      for (int i = 0; i < glyphCount; i++) {
      }

      float runSize = runningPositionX / 64f;
      return new ShapedText(
        glyphs, positionsX, positionsY, runSize,
        text, fontIdentifier);
    } finally {
      hb_buffer_destroy(hbBuffer);
    }
  }

}
