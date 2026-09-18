package net.buildabrowser.babbrowser.textshaping.core;

public record ShapedText(
  short[] glyphs,
  float[] positionsX,
  float[] positionsY,
  float runSize,
  String fallbackText,
  FontIdentifier fontIdentifier
) {
  
}
