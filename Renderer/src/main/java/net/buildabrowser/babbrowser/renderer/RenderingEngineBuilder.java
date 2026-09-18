package net.buildabrowser.babbrowser.renderer;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchPolicy;
import net.buildabrowser.babbrowser.fetch.UAChooser;
import net.buildabrowser.babbrowser.html.ua.DownloadManager;
import net.buildabrowser.babbrowser.html.ua.TabManager;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine.ResourceResolver;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.content.input.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.imp.RenderingEngineBuilderImp;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.renderer.uistate.Frame;

public interface RenderingEngineBuilder {

  /**
   * Set the fetch backend, allowing the engine to make network and FS requests.
   * @param backend The fetch backend
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setFetchBackend(FetchBackend backend);

  /**
   * Set the fetch policy, which allows enabling/disabling cookies and overriding fetch responses
   * (for example, to return a network error for a disallowed request).
   * @param policy The fetch policy
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setFetchPolicy(FetchPolicy policy);

  /**
   * Set the cookie store, which allows determining how cookies are stored.
   * There are 3 pre-made cookie stores
   * NoOpCookieStore - Disables cookies
   * InMemoryCookieStore - Remember cookies until the renderer is closed, and then discard them
   * SQLiteCookieStore (Requires :SQLiteCookieStore module) - Persist cookies into an SQL database,
   *  allowing access across multiple sessions.
   * @param cookieStore The cookie store
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setCookieStore(CookieStore cookieStore);
  
  /**
   * Set the UA chooser, which is responsible for determining what User-Agent should be attached to a given request.
   * @param uaChooser The UA chooser
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setUAChooser(UAChooser uaChooser);

  /**
   * Set the thread group supplier, a supplier that returns the ExecutorService used internally for threading.
   * @param threadGroupSupplier The thread group supplier.
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setThreadGroupSupplier(Supplier<ExecutorService> threadGroupSupplier);

  /**
   * Set the painter, responsible for creating graphics on the screen (or other medium, for esoteric use cases).
   * @param painter The painter
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setPainter(Painter painter);

  /**
   * Set the document loader registry, responsible for determining how navigables load a given mime type.
   * Get an instance of this with `DocumentLoaderRegistry.createDefault()`. Register additional loaders
   * using DocumentLoaderRegistry#register.
   * @param documentLoaderRegistry The document loader registry
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setDocumentLoaderRegistry(DocumentLoaderRegistry documentLoaderRegistry);
  
  /**
   * Set the resource resolver, whose sole job is to resolve resources that were bundled in
   * the application's resource directory.
   * @param resourceResolver The resource resolver
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setResourceResolver(ResourceResolver resourceResolver);

  /**
   * Set the clipboard provider. When the user attempts to select and copy data, the clipboard provider
   * is expected to add that data to the system clipboard.
   * @param clipboardProvider The clipboard provider
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setClipboardProvider(ClipboardProvider<?> clipboardProvider);
  
  /**
   * Set the virtual keyboard factory. The factory creates an instance of a VirtualKeyboard, which
   * is used to integrate with software keyboards that expect to mutate software state instead of
   * sending raw key events.
   * @param virtualKeyboardFactory The virtual keyboard factory
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setVirtualKeyboardFactory(Function<Frame, VirtualKeyboard> virtualKeyboardFactory);

  /**
   * Set the tab manager. Used to handle requests from a web page to open a new tab.
   * @param tabManager The tab manager
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setTabManager(TabManager tabManager);

  /**
   * Set the download manager. The download manager is responsible for determing where a user would like to
   * store downloads, and then streaming the download request to its destination. The download manager also
   * has default methods that can be overridden to block downloads.
   * @param downloadManager The download manager
   * @return The builder instance (for chaining)
   */
  RenderingEngineBuilder setDownloadManager(DownloadManager downloadManager);

  RenderingEngine build();

  static RenderingEngineBuilder create() {
    return new RenderingEngineBuilderImp();
  }
  
}
