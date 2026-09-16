package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.net.URI;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.browser.imp.BrowserInstanceImp;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.cookies.exception.CookieStoreException;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;

public interface BrowserInstance {
  
  RenderingEngine getRenderingEngine();

  WindowSet windowSet();

  static BrowserInstance create(
    URI profilePath,
    ComponentPainter<Component> painter,
    CookieStore cookieStore,
    A11YProvider a11yProvider
  ) throws CookieStoreException {
    cookieStore.initialize();
    return new BrowserInstanceImp(
      profilePath, painter, cookieStore, a11yProvider);
  }

}
