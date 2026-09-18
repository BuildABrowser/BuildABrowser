package net.buildabrowser.babbrowser.renderer.paint.test;

import net.buildabrowser.babbrowser.renderer.layout.FontLoader;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;

public class TestFontLoader implements FontLoader {

  private final LoadedFont testFont;

  public TestFontLoader(LoadedFont testFont) {
    this.testFont = testFont;
  }

  @Override
  public LoadedFont load(FontOptions options) {
    return testFont;
  }

}
