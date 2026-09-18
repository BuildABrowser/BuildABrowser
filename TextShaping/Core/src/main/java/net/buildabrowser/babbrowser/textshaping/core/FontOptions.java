package net.buildabrowser.babbrowser.textshaping.core;

import java.util.List;

public record FontOptions(
  List<FontFamily> families, float size, int weight
) {
  
}