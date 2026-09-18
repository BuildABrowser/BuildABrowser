package net.buildabrowser.babbrowser.renderer.content.common.test;

import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.ShapedText;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;
import net.buildabrowser.babbrowser.textshaping.core.TextRuns;

public class TestLoadedFont implements LoadedFont {

  private final FontMetrics testMetrics;

  public TestLoadedFont(FontMetrics testMetrics) {
    this.testMetrics = testMetrics;
  }
  
  @Override
  public FontMetrics metrics() {
    return testMetrics;
  }

  @Override
  public TextRuns shape(String text) {
    float runSize = testMetrics.stringWidth(text);
    return new TextRuns(new TextRun(0, 0, new ShapedText(null, null, null, runSize, text, null)), runSize);
  }

}
