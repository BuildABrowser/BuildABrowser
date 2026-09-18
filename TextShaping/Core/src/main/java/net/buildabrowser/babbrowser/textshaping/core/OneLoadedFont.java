package net.buildabrowser.babbrowser.textshaping.core;

import java.io.Closeable;

public interface OneLoadedFont extends Closeable {

  FontMetrics metrics();

  TextShaper shaper();

  short getUTF32Glyph(int codePoint);

}
