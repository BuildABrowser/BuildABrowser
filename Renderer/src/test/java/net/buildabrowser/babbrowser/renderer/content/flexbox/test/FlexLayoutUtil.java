package net.buildabrowser.babbrowser.renderer.content.flexbox.test;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.test.LayoutContextTestUtil;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContextGenerator;

public final class FlexLayoutUtil {
  
  private FlexLayoutUtil() {}

  public static BoxFragment<?> doLayout(ElementBox parentBox) {
    return doLayoutConstrained(parentBox, LayoutConstraint.AUTO, LayoutConstraint.AUTO).childFragments();
  }

  public static FlexTestLayoutResult doLayoutSized(ElementBox parentBox, float width) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.AUTO);
  }

  public static FlexTestLayoutResult doLayoutSized(ElementBox parentBox, float width, float height) {
    return doLayoutConstrained(parentBox, LayoutConstraint.of(width), LayoutConstraint.of(height));
  }

  public static FlexTestLayoutResult doLayoutConstrained(
    ElementBox parentBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    LayoutContext layoutContext = LayoutContextTestUtil.createTestLayoutContext(
      widthConstraint, heightConstraint);
    LayoutContextGenerator.generateLayoutContexts(parentBox, layoutContext);
    FlexBoxContent content = (FlexBoxContent) parentBox.content();

    content.fixupChildren(parentBox);
    FlexBoxFragment dimensionFrag = (FlexBoxFragment) parentBox.layout(widthConstraint, heightConstraint);
    return new FlexTestLayoutResult(dimensionFrag, dimensionFrag.innerFragment(), content);
  }

  public static record FlexTestLayoutResult(
    FlexBoxFragment flexboxFragment, BoxFragment<?> childFragments, FlexBoxContent rootContent
  ) {}

}
