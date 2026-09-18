package net.buildabrowser.babbrowser.textshaping.core;

public interface TextShaperLoader {
  
  OneLoadedFont getOrLoad(
    FontResource resource,
    FontOptions fontOptions
  );

}
