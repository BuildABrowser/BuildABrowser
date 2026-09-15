package net.buildabrowser.babbrowser.browser.imp;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.html.ua.TabManager;

public class TabManagerImp implements TabManager {

  private final WindowSet windowSet;

  public TabManagerImp(
    WindowSet windowSet
  ) {
    this.windowSet = windowSet;
  }

  @Override
  public Navigable addTopLevelTraversable(Navigable sourceNavigable) {
    Tab tab = windowSet.openTabAfter(sourceNavigable.uuid());
    return tab.getFrame().navigable();
  }
  
}
