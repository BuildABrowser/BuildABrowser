package net.buildabrowser.babbrowser.a11y.accesskit;

import java.util.ArrayList;

import com.zaxxer.sparsebits.SparseBitSet;

import net.buildabrowser.babbrowser.dom.Node;

public class AKNodeRegistry {
  
  private final ArrayList<Node> nodeList = new ArrayList<>(128);
  private final SparseBitSet activeIds = new SparseBitSet();

  private boolean isSorted = false;

  public void restart() {
    // TODO: Downsize the array list if it's not be fully utilized for a while
    nodeList.clear();
    activeIds.clear();
  }

  public void pushNode(long nodeId, Node node) {
    assert nodeId <= Integer.MAX_VALUE;
    nodeList.add(node);
    assert !activeIds.get((int) nodeId);
    activeIds.set((int) nodeId);
    this.isSorted = false;
  }

  // TODO: This is likely quite inefficient
  // (but still better than a HashMap)
  public Node getNode(long nodeId) {
    assert nodeId <= Integer.MAX_VALUE;
    if (!activeIds.get((int) nodeId)) {
      return null;
    }

    if (!isSorted) {
      nodeList.sort((a, b) -> Long.compare(a.ariaId(), b.ariaId()));
    }

    int listPos = activeIds.get(0, (int) nodeId).cardinality();
    return nodeList.get(listPos);
  }

}
