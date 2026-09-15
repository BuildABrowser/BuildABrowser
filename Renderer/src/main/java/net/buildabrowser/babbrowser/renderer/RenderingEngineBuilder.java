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

  RenderingEngineBuilder setFetchBackend(FetchBackend backend);

  RenderingEngineBuilder setFetchPolicy(FetchPolicy policy);

  RenderingEngineBuilder setCookieStore(CookieStore cookieStore);
  
  RenderingEngineBuilder setUAChooser(UAChooser uaChooser);

  RenderingEngineBuilder setThreadGroupSupplier(Supplier<ExecutorService> threadGroupSupplier);

  RenderingEngineBuilder setPainter(Painter painter);

  RenderingEngineBuilder setDocumentLoaderRegistry(DocumentLoaderRegistry documentLoaderRegistry);
  
  RenderingEngineBuilder setResourceResolver(ResourceResolver resourceResolver);

  RenderingEngineBuilder setClipboardProvider(ClipboardProvider<?> clipboardProvider);
  
  RenderingEngineBuilder setVirtualKeyboardFactory(Function<Frame, VirtualKeyboard> virtualKeyboardFactory);

  RenderingEngineBuilder setTabManager(TabManager tabManager);

  RenderingEngineBuilder setDownloadManager(DownloadManager downloadManager);

  RenderingEngine build();

  static RenderingEngineBuilder create() {
    return new RenderingEngineBuilderImp();
  }
  
}
