package net.buildabrowser.babbrowser.a11y.core;

import java.io.Closeable;

import net.buildabrowser.babbrowser.a11y.core.aom.AriaEvent;
import net.buildabrowser.babbrowser.dom.Node;

public interface A11YFrame extends Closeable {

  A11YFocusManager a11yFocusManager();
  
  void update(Node node);

  void fireNodeEvent(long nodeId, AriaEvent event);

}
