package net.buildabrowser.babbrowser.painter.core;

import net.buildabrowser.babbrowser.textshaping.core.FontFamily;
import net.buildabrowser.babbrowser.textshaping.core.FontOptions;
import net.buildabrowser.babbrowser.textshaping.core.FontResource;

public interface FontResourceLoader {

  FontResource load(FontFamily family, FontOptions options);

}
