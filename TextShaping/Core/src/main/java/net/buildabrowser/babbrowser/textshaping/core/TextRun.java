package net.buildabrowser.babbrowser.textshaping.core;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class TextRun implements IntrusiveList<TextRun> {
  
  private final float offsetX;
  private final float offsetY;
  private final ShapedText shapedText;

  private TextRun next;

  public TextRun(
    float offsetX, float offsetY,
    ShapedText shapedText
  ) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.shapedText = shapedText;
  }

  public float offsetX() {
    return this.offsetX;
  }

  public float offsetY() {
    return this.offsetY;
  }
  
  public ShapedText shapedText() {
    return this.shapedText;
  }

  @Override
  public TextRun next() {
    return this.next;
  }

  @Override
  public void setNext(TextRun next) {
    this.next = next;
  }

}
