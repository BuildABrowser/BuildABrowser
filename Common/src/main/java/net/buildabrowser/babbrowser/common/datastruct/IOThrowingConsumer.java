package net.buildabrowser.babbrowser.common.datastruct;

import java.io.IOException;

public interface IOThrowingConsumer<T> {
  
  void accept(T object) throws IOException;

}
