package net.buildabrowser.babbrowser.browser.imp;

import java.awt.Component;
import java.net.URI;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.browser.BrowserInstance;
import net.buildabrowser.babbrowser.browser.net.UAChooserImp;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.embedding.swing.SwingEmbedding;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.RenderingEngineBuilder;

public class BrowserInstanceImp implements BrowserInstance {

  private final RenderingEngine renderingEngine;
  private final WindowSet windowSet;

  public BrowserInstanceImp(
    URI profilePath,
    ComponentPainter<Component> painter,
    CookieStore cookieStore,
    A11YProvider a11yProvider
  ) {
    this.windowSet = WindowSet.create(this);

    RenderingEngineBuilder builder = RenderingEngineBuilder.create();
    SwingEmbedding.configure(builder);
    builder
      .setPainter(painter)
      .setCookieStore(cookieStore)
      .setA11YProvider(a11yProvider)
      .setTabManager(new TabManagerImp(windowSet))
      .setUAChooser(new UAChooserImp())
      .setDownloadManager(new DownloadManagerImp());
    this.renderingEngine = builder.build();
  }

  @Override
  public RenderingEngine getRenderingEngine() {
    return this.renderingEngine;
  }

  @Override
  public WindowSet windowSet() {
    return this.windowSet;
  }
  
}
