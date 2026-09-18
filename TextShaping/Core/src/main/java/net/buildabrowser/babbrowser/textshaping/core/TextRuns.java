package net.buildabrowser.babbrowser.textshaping.core;

public record TextRuns(
  TextRun runs,
  float runSize
) {

  public static float posForOffset(TextRun textRuns, int offset) {
    if (offset <= 0) return 0;
    
    float pos = 0;
    TextRun nextRun = textRuns;
    while (nextRun != null) {
      TextRun textRun = nextRun;
      nextRun = nextRun.next();

      ShapedText shape = textRun.shapedText();
      if (offset >= shape.glyphs().length) {
        pos += shape.runSize();
        offset -= shape.glyphs().length;
      } else {
        pos += shape.positionsX()[offset];
        offset = 0;
      }
      if (offset == 0) return pos;
    }

    return pos;
  }

  public static int offsetForPos(TextRun textRuns, float targetPos) {
    if (targetPos <= 0) return 0;

    int offset = 0;
    float pos = targetPos;
    TextRun nextRun = textRuns;
    while (nextRun != null) {
      TextRun textRun = nextRun;
      nextRun = nextRun.next();

      ShapedText shape = textRun.shapedText();
      float runSize = shape.runSize();
      int glyphCount = shape.glyphs().length;

      if (pos >= runSize) {
        pos -= runSize;
        offset += glyphCount;
      } else {
        float[] positions = shape.positionsX();
        for (int i = 0; i < glyphCount; i++) {
          float left = positions[i];
          float right = (i + 1 < glyphCount) ? positions[i + 1] : runSize;
          float mid = (left + right) / 2;

          if (pos < mid) return offset + i;
        }
        return offset + glyphCount;
      }
    }

    return offset;
}
  
}
