package net.buildabrowser.babbrowser.a11y.core;

import net.buildabrowser.babbrowser.a11y.core.aom.AriaProperty;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;
import net.buildabrowser.babbrowser.dom.Node;

public interface AriaCallbacks<T> {
  
  T visitNode(T parent, long nodeId, Node node, AriaRole role);

  void exitNode(T node, long nodeId);

  void visitAttribute(T node, AriaProperty property, String value);

  void visitText(T node, String value);

}
