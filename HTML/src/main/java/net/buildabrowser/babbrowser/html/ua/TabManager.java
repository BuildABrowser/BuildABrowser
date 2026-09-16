package net.buildabrowser.babbrowser.html.ua;

import net.buildabrowser.babbrowser.html.navigation.Navigable;

public interface TabManager {

  // Return null to block popup
  Navigable addTopLevelTraversable(Navigable sourceNavigable);
  
}
