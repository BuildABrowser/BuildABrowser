package net.buildabrowser.babbrowser.renderer.content.generic;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public interface GenericItem {

  ElementBox box();
  
  UnmanagedBoxFragment<?> fragment();

  float crossSize();

  void setCrossPos(float itemCrossPos);

  LayoutConstraint firstMarginCross(LayoutConstraint parentSize);

  LayoutConstraint secondMarginCross(LayoutConstraint parentSize);

}
