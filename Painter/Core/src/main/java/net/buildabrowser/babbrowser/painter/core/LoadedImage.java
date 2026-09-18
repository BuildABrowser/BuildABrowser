package net.buildabrowser.babbrowser.painter.core;

import java.io.IOException;
import java.io.InputStream;

import net.buildabrowser.babbrowser.common.datastruct.IOThrowingConsumer;

public interface LoadedImage {
 
  int width();

  int height();

  void streamData(IOThrowingConsumer<InputStream> dataReader) throws IOException;

}
