package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappingRLEBuffer;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;

public class FlowTextFragmentBuilder {

  private MappingRLEBuffer originalRleBuffer;
  private MappingRLEBuffer rleBuffer;

  private TextRun textRuns = null;
  private TextRun lastRun = null;
  private Node sourceNode = null;
  private int lastIndex = 0;

  private float width = 0;
  private float height = 0;

  // TODO: Compact text runs
  public void startText(
    Node sourceNode,
    MappingRLEBuffer reference
  ) {
    assert this.textRuns == null;
    this.textRuns = null;
    this.lastRun = null;
    this.sourceNode = sourceNode;
    this.originalRleBuffer = reference;
  }

  public void addText(
    TextRun textRuns,
    int sourceStartIndex, int sourceEndIndex,
    float width, float height
  ) {
    assert sourceStartIndex >= lastIndex || sourceNode == null;
    if (
      this.originalRleBuffer != null
      && this.rleBuffer == null
      && (sourceStartIndex > 0 || textRuns != null)
    ) {
      this.rleBuffer = originalRleBuffer.clone();
    }
    if (rleBuffer != null && sourceStartIndex > lastIndex) {
      rleBuffer.deleteRange(lastIndex, sourceStartIndex - 1);
    }
    this.lastIndex = sourceEndIndex;

    TextRun nextRun = textRuns;
    while (nextRun != null) {
      TextRun textRun = nextRun;
      nextRun = nextRun.next();
      
      float textRunX = textRun.offsetX() + this.width;
      float textRunY = textRun.offsetY();
      // Cannot re-use existing text run, due to intrusive list pointer
      TextRun newRun = new TextRun(
        textRunX, textRunY,
        textRun.shapedText());
      if (this.textRuns == null) {
        this.textRuns = lastRun = newRun;
      } else {
        lastRun.setNext(newRun);
        this.lastRun = newRun;
      }
    }

    this.width += width;
    this.height = Math.max(this.height, height);
  }

  public boolean isEmpty() {
    return textRuns == null;
  }

  public float height() {
    return this.height;
  }

  public TextFragment commit(FontMetrics fontMetrics) {
    boolean skipRuns =
      rleBuffer == null
      || sourceNode == null
      || (
        sourceNode instanceof Text text
        && rleBuffer.matchesSource(text.data().length()));
    short[] sourceRuns = skipRuns ?
      null : rleBuffer.toShortArray();
    // TODO: Also need to delete tail

    float firstBaseline = 0; // TODO: Compute first baseline
    float lastBaseline = fontMetrics.descent(); // TODO: Respect fallback fonts

    // TextFragmentMerger.mergeTextRuns(textRuns);
    TextFragment result = new TextFragment(
      width, height,
      firstBaseline, lastBaseline,
      sourceNode, textRuns, sourceRuns);
    this.lastIndex = 0;
    this.rleBuffer = null;
    this.width = 0;
    this.height = 0;
    this.textRuns = null;
    this.lastRun = null;

    return result;
  }

}
