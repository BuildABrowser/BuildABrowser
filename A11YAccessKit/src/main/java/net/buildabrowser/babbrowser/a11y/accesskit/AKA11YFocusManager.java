package net.buildabrowser.babbrowser.a11y.accesskit;

import java.util.Optional;

import net.buildabrowser.ak4j.AK4JHandle;
import net.buildabrowser.ak4j.AKTextSelection;
import net.buildabrowser.babbrowser.a11y.core.A11YCallbacks;
import net.buildabrowser.babbrowser.a11y.core.A11YFocusManager;
import net.buildabrowser.babbrowser.dom.Node;

public class AKA11YFocusManager implements A11YFocusManager {

  private final AK4JHandle ak4jHandle;
  private final AKNodeRegistry nodeRegistry;

  private boolean focused = true;
  private Node focusedNode;
  private AKTextSelection textSelection;
  private Optional<A11YCallbacks> callbacks = Optional.empty();

  public AKA11YFocusManager(
    AK4JHandle ak4jHandle,
    AKNodeRegistry nodeRegistry
  ) {
    this.ak4jHandle = ak4jHandle;
    this.nodeRegistry = nodeRegistry;
    focus();
  }

  @Override
  public Node focusedNode() {
    return this.focusedNode;
  }

  @Override
  public long focusedNodeId() {
    return (focusedNode == null ? 0 : focusedNode.ariaId());
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

    System.out.println("Focused " + focused);
    ak4jHandle.adapter().setFocus(focused);
  }

  @Override
  public void focus() {
    if (!focused) {
      ak4jHandle.adapter().setFocus(true);
      this.focused = true;
      callbacks.ifPresent(c -> c.onFocused());
    }
  }

  @Override
  public void blur() {
    if (focused) {
      ak4jHandle.adapter().setFocus(false);
      this.focused = false;
      // TODO: NONE
      callbacks.ifPresent(c -> c.onBlur());
    }
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
