package net.buildabrowser.babbrowser.renderer.content.common.test;

import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.paint.test.TestFontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;

public final class LayoutContextTestUtil {
  
  private LayoutContextTestUtil() {}

  public static LayoutContext createTestLayoutContext(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    FontMetrics testMetrics = TestFontMetrics.create(10, 5);
    LoadedFont testFont = new TestLoadedFont(testMetrics);
    Viewport viewport = new Viewport(0, 0, (int) widthConstraint.value(), (int) heightConstraint.value());
    FragmentFactory fragmentFactory = FragmentFactory.createDefault();
    LayoutContext layoutContext = new LayoutContext(
      new GlobalLayoutContext(
        null, _1 -> testFont,
        (m, s) -> m.shape(s),
        viewport, null, null, null, fragmentFactory),
      testFont, testMetrics);
    return layoutContext;
  }

}
