package net.buildabrowser.babbrowser.a11y.core;

import net.buildabrowser.babbrowser.a11y.core.aom.AriaEvent;
import net.buildabrowser.babbrowser.dom.Node;

public class DummyA11YFrame implements A11YFrame {

  @Override
  public A11YFocusManager a11yFocusManager() {
    return null; // TODO: Return valid focus manager?
  }

  @Override
  public void update(Node node) {}

  @Override
  public void close() {}

  @Override
  public void fireNodeEvent(long nodeId, AriaEvent event) {}

}
