package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.textshaping.core.FontMetrics;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;

public record LayoutContext(
  GlobalLayoutContext global,
  LoadedFont font,
  FontMetrics rootMetrics
) {

}
