package net.buildabrowser.babbrowser.renderer.imp;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.a11y.core.A11YProvider;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchConfig;
import net.buildabrowser.babbrowser.fetch.FetchPolicy;
import net.buildabrowser.babbrowser.fetch.UAChooser;
import net.buildabrowser.babbrowser.html.ua.DownloadManager;
import net.buildabrowser.babbrowser.html.ua.TabManager;
import net.buildabrowser.babbrowser.html.ua.UAUIFeatures;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.RenderingEngine.ResourceResolver;
import net.buildabrowser.babbrowser.renderer.RenderingEngineBuilder;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.content.input.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public class RenderingEngineBuilderImp implements RenderingEngineBuilder {

  private FetchBackend fetchBackend;
  private FetchPolicy fetchPolicy;
  private CookieStore cookieStore;
  private A11YProvider a11yProvider;
  private UAChooser uaChooser;
  private Supplier<ExecutorService> threadGroupSupplier;
  private Painter painter;
  private DocumentLoaderRegistry documentLoaderRegistry;
  private ResourceResolver resourceResolver;
  private ClipboardProvider<?> clipboardProvider;
  private Function<Frame, VirtualKeyboard> virtualKeyboardFactory;
  private TabManager tabManager;
  private DownloadManager downloadManager;

  @Override
  public RenderingEngineBuilder setFetchBackend(FetchBackend backend) {
    this.fetchBackend = backend;
    return this;
  }

  @Override
  public RenderingEngineBuilder setFetchPolicy(FetchPolicy policy) {
    this.fetchPolicy = policy;
    return this;
  }

  @Override
  public RenderingEngineBuilder setCookieStore(CookieStore cookieStore) {
    this.cookieStore = cookieStore;
    return this;
  }

  @Override
  public RenderingEngineBuilder setA11YProvider(A11YProvider a11yProvider) {
    this.a11yProvider = a11yProvider;
    return this;
  }

  @Override
  public RenderingEngineBuilder setUAChooser(UAChooser uaChooser) {
    this.uaChooser = uaChooser;
    return this;
  }

  @Override
  public RenderingEngineBuilder setThreadGroupSupplier(Supplier<ExecutorService> threadGroupSupplier) {
    this.threadGroupSupplier = threadGroupSupplier;
    return this;
  }

  @Override
  public RenderingEngineBuilder setPainter(Painter painter) {
    this.painter = painter;
    return this;
  }

  @Override
  public RenderingEngineBuilder setDocumentLoaderRegistry(DocumentLoaderRegistry documentLoaderRegistry) {
    this.documentLoaderRegistry = documentLoaderRegistry;
    return this;
  }

  @Override
  public RenderingEngineBuilder setResourceResolver(ResourceResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
    return this;
  }

  @Override
  public RenderingEngineBuilder setClipboardProvider(ClipboardProvider<?> clipboardProvider) {
    this.clipboardProvider = clipboardProvider;
    return this;
  }

  @Override
  public RenderingEngineBuilder setVirtualKeyboardFactory(Function<Frame, VirtualKeyboard> virtualKeyboardFactory) {
    this.virtualKeyboardFactory = virtualKeyboardFactory;
    return this;
  }

  @Override
  public RenderingEngineBuilder setTabManager(TabManager tabManager) {
    this.tabManager = tabManager;
    return this;
  }

  @Override
  public RenderingEngineBuilder setDownloadManager(DownloadManager downloadManager) {
    this.downloadManager = downloadManager;
    return this;
  }

  @Override
  public RenderingEngine build() {
    checkNotNull(fetchBackend, "fetchBackend");
    checkNotNull(fetchPolicy, "fetchPolicy");
    checkNotNull(cookieStore, "cookieStore");
    checkNotNull(a11yProvider, "a11yProvider");
    checkNotNull(uaChooser, "uaChooser");
    checkNotNull(threadGroupSupplier, "threadGroupSupplier");
    checkNotNull(painter, "painter");
    checkNotNull(documentLoaderRegistry, "documentLoaderRegistry");
    checkNotNull(resourceResolver, "resourceResolver");
    checkNotNull(clipboardProvider, "clipboardProvider");
    checkNotNull(virtualKeyboardFactory, "virtualKeyboardFactory");
    checkNotNull(tabManager, "tabManager");
    checkNotNull(downloadManager, "downloadManager");

    FetchConfig fetchConfig = new FetchConfig(
      fetchBackend, fetchPolicy, cookieStore, uaChooser);
    return RenderingEngine.create(
      fetchConfig,
      threadGroupSupplier,
      painter,
      a11yProvider,
      documentLoaderRegistry,
      resourceResolver,
      clipboardProvider,
      virtualKeyboardFactory,
      new UAUIFeatures(tabManager, downloadManager));
  }

  private void checkNotNull(Object o, String oName) {
    if (o != null) return;
    throw new IllegalStateException("Cannot build: item '" + oName + "' not set.");
  }
  
}
