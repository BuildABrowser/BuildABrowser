package net.buildabrowser.babbrowser.a11y.accesskit;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import net.buildabrowser.ak4j.AK4JHandle;
import net.buildabrowser.ak4j.AKAction;
import net.buildabrowser.ak4j.AKRole;
import net.buildabrowser.ak4j.AKTextSelection;
import net.buildabrowser.ak4j.AKTextSelection.AKTextPosition;
import net.buildabrowser.ak4j.util.TextRunUtil;
import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.a11y.core.AriaCallbacks;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaProperty;
import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;

public class AKAriaCallbacks implements AriaCallbacks<MemorySegment> {

  private final AK4JHandle ak4jHandle;
  private final AKNodeRegistry nodeRegistry;
  private final AKA11YFocusManager focusManager;
  private final A11YOps ops;
  private final MemorySegment treeUpdate;
  private final Arena scope;

  public AKAriaCallbacks(
    AK4JHandle ak4jHandle,
    AKNodeRegistry nodeRegistry,
    AKA11YFocusManager focusManager,
    A11YOps ops,
    MemorySegment treeUpdate,
    Arena scope
  ) {
    this.ak4jHandle = ak4jHandle;
    this.nodeRegistry = nodeRegistry;
    this.focusManager = focusManager;
    this.ops = ops;
    this.treeUpdate = treeUpdate;
    this.scope = scope;
  }

  @Override
  public MemorySegment visitNode(MemorySegment parent, long nodeId, Node node, AriaRole role) {
    AKRole mappedRole = AKRoleMapper.map(role);
    nodeRegistry.pushNode(nodeId, node);
    ak4jHandle.nodes().pushChild(parent, nodeId);

    MemorySegment nodePtr = ak4jHandle.nodes().create(mappedRole, scope);

    if (node instanceof Element element) {
      ak4jHandle.nodes().setHTMLTag(nodePtr, element.name(), scope);

      // TODO: Need to filter to a elements?
      String hrefAttr = element.getAttribute("href");
      if (hrefAttr != null) {
        ak4jHandle.nodes().setHref(nodePtr, hrefAttr, scope);
        ak4jHandle.nodes().addAction(nodePtr, AKAction.FOCUS);
      }

      if (isActivatable(element)) {
        ak4jHandle.nodes().addAction(nodePtr, AKAction.FOCUS);
        ak4jHandle.nodes().addAction(nodePtr, AKAction.CLICK);
      }
    }

    if (
      nodeId == focusManager.focusedNodeId()
      && focusManager.textSelection() != null
    ) {
      ak4jHandle.nodes().setTextSelection(
        nodePtr, focusManager.textSelection(), scope);
    } else if (nodeId == focusManager.focusedNodeId()) {
      Node child = node.firstChild();
      while (child != null) {
        if (child instanceof Text text && text.toString().trim().length() > 0) {
          ak4jHandle.nodes().setTextSelection(
            nodePtr,
            new AKTextSelection(
              new AKTextPosition(child.ariaId(), 0),
              new AKTextPosition(child.ariaId(), 0)), scope);
          break;
        }
        child = child.nextSibling();
      }
    }

    return nodePtr;
  }

  @Override
  public void exitNode(MemorySegment node, long nodeId) {
    ak4jHandle.pushTreeUpdateNode(treeUpdate, nodeId, node);
  }

  @Override
  public void visitAttribute(MemorySegment node, AriaProperty property, String value) {
    // TODO
  }

  @Override
  public void visitText(MemorySegment node, String value) {
    byte[] lengths = new byte[value.length()];
    int lengthsLength = TextRunUtil.getTextLengths(value, lengths);
    ak4jHandle.nodes().setValue(node, value, scope);
    ak4jHandle.nodes().setCharacterLengths(node, lengths, lengthsLength, scope);
  }

  private boolean isActivatable(Element element) {
    return ops.isActivatable(element);
  }
  
}
