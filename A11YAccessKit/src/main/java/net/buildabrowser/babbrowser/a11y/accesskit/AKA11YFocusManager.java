package net.buildabrowser.babbrowser.a11y.accesskit;

import java.util.Optional;

import net.buildabrowser.ak4j.AKTextSelection;
import net.buildabrowser.babbrowser.a11y.core.A11YCallbacks;
import net.buildabrowser.babbrowser.a11y.core.A11YFocusManager;
import net.buildabrowser.babbrowser.dom.Node;

public class AKA11YFocusManager implements A11YFocusManager {

  private final AKNodeRegistry nodeRegistry;

  private boolean focused = true;
  private Node focusedNode;
  private AKTextSelection textSelection;
  private Optional<A11YCallbacks> callbacks = Optional.empty();

  public AKA11YFocusManager(AKNodeRegistry nodeRegistry) {
    this.nodeRegistry = nodeRegistry;
    unblur();
  }

  @Override
  public Node focusedNode() {
    return this.focusedNode;
  }

  @Override
  public long focusedNodeId() {
    return focused ?
      (focusedNode == null ? 2 : focusedNode.ariaId()) :
      2; // TODO: NONE
  }

  @Override
  public void update(Node node) {
    if (node == null) {
      focusedNode = null;
      callbacks.ifPresent(c -> c.onNodeFocused(null));
      return;
    }

    if (
      focusedNode == null
      // TODO: Check if node still connected to document
      || focusedNode.nodeDocument() != node.nodeDocument()
    ) {
      focusedNode = node.nodeDocument();
      callbacks.ifPresent(c -> c.onNodeFocused(focusedNode));
    }
  }

  @Override
  public void unblur() {
    this.focused = true;
    callbacks.ifPresent(c -> c.onUnblur());
  }

  @Override
  public void blur() {
    this.focused = false;
    // TODO: NONE
    callbacks.ifPresent(c -> c.onBlur());
  }

  @Override
  public void focusNodeByAriaId(long nodeId) {
    this.focusedNode = nodeRegistry.getNode(nodeId);
    callbacks.ifPresent(c -> c.onNodeFocused(focusedNode));
  }

  @Override
  public void attachCallbacks(A11YCallbacks callbacks) {
    this.callbacks = Optional.of(callbacks);
  }

  public void updateTextSelection(AKTextSelection textSelection) {
    this.textSelection = textSelection;
  }

  public AKTextSelection textSelection() {
    return this.textSelection;
  }

}
