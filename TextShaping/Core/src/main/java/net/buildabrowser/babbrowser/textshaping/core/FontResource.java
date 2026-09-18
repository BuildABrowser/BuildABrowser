package net.buildabrowser.babbrowser.textshaping.core;

import java.nio.ByteBuffer;

public interface FontResource {

  FontIdentifier fontIdentifier();

  ByteBuffer syncBuffer();
  
}
