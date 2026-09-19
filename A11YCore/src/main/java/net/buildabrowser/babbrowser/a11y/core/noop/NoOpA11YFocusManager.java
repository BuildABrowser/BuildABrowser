package net.buildabrowser.babbrowser.a11y.core.noop;

import net.buildabrowser.babbrowser.a11y.core.A11YCallbacks;
import net.buildabrowser.babbrowser.a11y.core.A11YFocusManager;
import net.buildabrowser.babbrowser.dom.Node;

public class NoOpA11YFocusManager implements A11YFocusManager {

  @Override
  public Node focusedNode() {
    return null;
  }

  @Override
  public long focusedNodeId() {
    return 0;
  }

  @Override
  public void update(Node node) {}

  @Override
  public void focus() {}

  @Override
  public void blur() {}

  @Override
  public void focusNodeByAriaId(long nodeId) {}

  @Override
  public void attachCallbacks(A11YCallbacks callbacks) {}

}
