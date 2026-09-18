package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.List;

import net.buildabrowser.babbrowser.textshaping.core.ShapedText;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;

public class TextFragmentMerger {
  
  private TextFragmentMerger() {}

  public static void mergeTextRuns(List<TextRun> textRuns) {
    if (textRuns.size() <= 1) return;
    
    int runStart = 0;
    int runEnd = 0;
    TextRun refRun = null;
    for (int i = 0; i < textRuns.size(); i++) {
      TextRun currentRun = textRuns.get(i);
      if (
        refRun != null
        && currentRun.shapedText().fontIdentifier()
          != refRun.shapedText().fontIdentifier()
      ) {
        if (runStart < runEnd) {
          mergeTextRuns(textRuns, refRun, runStart, runEnd);
          i = runStart;
        }
        refRun = null;
      }
      if (refRun == null) {
        refRun = textRuns.get(i);
        runStart = i;
        runEnd = i;
      } else {
        runEnd++;
      }
    }

    if (refRun != null) {
      mergeTextRuns(textRuns, refRun, runStart, runEnd);
    }
  }

  private static void mergeTextRuns(
    List<TextRun> textRuns,
    TextRun refRun, int runStart, int runEnd
  ) {
    if (runStart >= runEnd) return;
    float baseOffsetX = refRun.offsetX();
    float baseOffsetY = refRun.offsetY();

    int glyphsSize = 0;
    int posXSize = 0;
    int posYSize = 0;
    float runSize = 0;
    int fallbackTextSize = 0;
    for (int i = runStart; i <= runEnd; i++) {
      TextRun currentRun = textRuns.get(i);
      ShapedText currentShape = currentRun.shapedText();
      glyphsSize += currentShape.glyphs().length;
      posXSize += currentShape.positionsX().length;
      posYSize += currentShape.positionsY().length;
      runSize += currentShape.runSize();
      fallbackTextSize += currentShape.fallbackText().length();
    }

    int glyphsPos = 0;
    short[] glyphs = new short[glyphsSize];
    int posXPos = 0;
    float[] positionX = new float[posXSize];
    int posYPos = 0;
    float[] positionY = new float[posYSize];
    StringBuilder fallbackTextBuilder = new StringBuilder(fallbackTextSize);
    for (int i = runStart; i <= runEnd; i++) {
      TextRun currentRun = textRuns.get(i);
      ShapedText currentShape = currentRun.shapedText();
      int currentGlyphsLen = currentShape.glyphs().length;
      System.arraycopy(
        currentShape.glyphs(), 0,
        glyphs, glyphsPos,
        currentGlyphsLen);
      glyphsPos += currentGlyphsLen;

      float offsetX = currentRun.offsetX() - baseOffsetX;
      for (float posX: currentShape.positionsX()) {
        positionX[posXPos++] = posX + offsetX;
      }

      float offsetY = currentRun.offsetY() - baseOffsetY;
      for (float posY: currentShape.positionsY()) {
        positionY[posYPos++] = posY + offsetY;
      }

      fallbackTextBuilder.append(currentShape.fallbackText());
    }

    ShapedText newShape = new ShapedText(
      glyphs, positionX, positionY, runSize,
      fallbackTextBuilder.toString(),
      refRun.shapedText().fontIdentifier());
    TextRun newRun = new TextRun(baseOffsetX, baseOffsetY, newShape);
    textRuns.set(runStart, newRun);
    // ... Java does not expose removeRange...
    textRuns.subList(runStart + 1, runEnd + 1).clear();
  }

}
