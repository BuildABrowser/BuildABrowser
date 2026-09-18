package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappingRLEBuffer;
import net.buildabrowser.babbrowser.textshaping.core.ShapedText;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;

public class TextFragment extends LayoutFragment {

  private final Node sourceNode;
  private final TextRun textRuns;
  private final short[] sourceRuns;

  private final float firstBaseline;
  private final float lastBaseline;

  public TextFragment(
    float width, float height,
    float firstBaseline, float lastBaseline,
    Node sourceNode, TextRun textRuns, short[] sourceRuns
  ) {
    super(width, height);
    this.firstBaseline = firstBaseline;
    this.lastBaseline = lastBaseline;
    this.sourceNode = sourceNode;
    this.textRuns = textRuns;
    this.sourceRuns = sourceRuns;
  }

  // For testing
  public TextFragment(
    float x, float y, float width, float height,
    String text
  ) {
    this(
      width, height,
      0, 0,
      null, createFakeTextRun(text), null);
    setPos(x, y);
  }

  @Override
  public float firstBaseline(Measurement measurement) {
    return this.firstBaseline;
  }

  @Override
  public float lastBaseline(Measurement measurement) {
    return this.lastBaseline;
  }

  public TextRun textRuns() {
    return this.textRuns;
  }

  public String _text() {
    StringBuilder allText = new StringBuilder();
    TextRun currentRun = textRuns;
    while (currentRun != null) {
      allText.append(currentRun.shapedText().fallbackText());
      currentRun = currentRun.next();
    }
    return allText.toString();
  }

  public Node sourceNode() {
    return this.sourceNode;
  }

  public int sourceIndex(int textIndex) {
    if (
      !(sourceNode instanceof Text)
      || sourceRuns == null
    ) return textIndex;

    return MappingRLEBuffer.sourceIndex(textIndex, sourceRuns);
  }

  public int textIndex(int sourceIndex) {
    if (
      !(sourceNode instanceof Text)
      || sourceRuns == null
    ) return sourceIndex;

    return MappingRLEBuffer.textIndex(sourceIndex, sourceRuns);
  }

  @Override
  public String toString() {
    return "[TextFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] text=[" + _text() + "]]";
  }

  private static TextRun createFakeTextRun(String text) {
    return new TextRun(0, 0, new ShapedText(
      null, null, null,
      0, text, null));
  }

}