package net.buildabrowser.babbrowser.a11y.core;

import net.buildabrowser.babbrowser.dom.Node;

public interface A11YCallbacks {

  default void onNodeFocused(Node node) {}

  default void onBlur() {}

  default void onFocused() {}
  
}
