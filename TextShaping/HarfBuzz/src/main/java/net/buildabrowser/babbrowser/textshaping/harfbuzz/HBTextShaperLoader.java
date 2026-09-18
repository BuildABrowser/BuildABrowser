package net.buildabrowser.babbrowser.textshaping.harfbuzz;

import static org.lwjgl.util.harfbuzz.HarfBuzz.HB_MEMORY_MODE_DUPLICATE;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_blob_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_blob_destroy;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_face_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_face_destroy;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_set_scale;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;
import net.buildabrowser.babbrowser.textshaping.core.OneLoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextShaper;
import net.buildabrowser.babbrowser.textshaping.core.TextShaperLoader;

public class HBTextShaperLoader implements TextShaperLoader {

  @Override 
  public OneLoadedFont getOrLoad(
    FontResource resource, FontOptions fontOptions
  ) {
    ByteBuffer buffer = resource.syncBuffer();
    
    ByteBuffer directBuffer;
    long tempMemory = 0L;
    if (buffer.isDirect()) {
      directBuffer = buffer;
    } else {
      tempMemory = MemoryUtil.nmemAlloc(buffer.remaining());
      directBuffer = MemoryUtil.memByteBuffer(tempMemory, buffer.remaining());
      directBuffer.put(buffer).flip();
    }

    try {
      long blob = hb_blob_create(directBuffer, HB_MEMORY_MODE_DUPLICATE, 0L, null);
      if (blob == 0L) throw new RuntimeException("Failed to create HarfBuzz blob.");

      long face = hb_face_create(blob, 0);
      long hbFont = hb_font_create(face);

      int scale = (int) (fontOptions.size() * 64);
      hb_font_set_scale(hbFont, scale, scale);

      hb_blob_destroy(blob);
      hb_face_destroy(face);
    
      TextShaper textShaper = new HBTextShaper(hbFont, resource.fontIdentifier());
      FontMetrics metrics = new HBFontMetrics(hbFont, textShaper, fontOptions);
      return new HBOneLoadedFont(hbFont, textShaper, metrics);
    } finally {
      if (tempMemory != 0L) {
        MemoryUtil.nmemFree(tempMemory);
      }
    }
  }

}