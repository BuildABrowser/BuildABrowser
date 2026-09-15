package net.buildabrowser.babbrowser.embedding.standardcommon.imp;

import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.ua.TabManager;

public class NoOpTabManager implements TabManager {

  @Override
  public Navigable addTopLevelTraversable(Navigable sourceNavigable) {
    return sourceNavigable;
  }
  
}
