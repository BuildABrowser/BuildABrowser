package net.buildabrowser.babbrowser.a11y.core.noop;

import net.buildabrowser.babbrowser.a11y.core.A11YFocusManager;
import net.buildabrowser.babbrowser.a11y.core.A11YFrame;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaEvent;
import net.buildabrowser.babbrowser.dom.Node;

public class NoOpA11YFrame implements A11YFrame {

  @Override
  public A11YFocusManager a11yFocusManager() {
    return new NoOpA11YFocusManager();
  }

  @Override
  public void update(Node node) {}

  @Override
  public void close() {}

  @Override
  public void fireNodeEvent(long nodeId, AriaEvent event) {}

}
