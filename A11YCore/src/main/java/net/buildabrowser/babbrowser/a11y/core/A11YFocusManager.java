package net.buildabrowser.babbrowser.a11y.core;

import net.buildabrowser.babbrowser.dom.Node;

public interface A11YFocusManager {

  Node focusedNode();

  long focusedNodeId();

  void update(Node node);

  void focus();

  void blur();

  void focusNodeByAriaId(long nodeId);

  void attachCallbacks(A11YCallbacks callbacks);

}
