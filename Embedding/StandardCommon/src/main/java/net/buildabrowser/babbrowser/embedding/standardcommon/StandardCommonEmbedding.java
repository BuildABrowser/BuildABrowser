package net.buildabrowser.babbrowser.embedding.standardcommon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.buildabrowser.babbrowser.cookies.stores.InMemoryCookieStore;
import net.buildabrowser.babbrowser.embedding.standardcommon.imp.BasicUAChooserImp;
import net.buildabrowser.babbrowser.embedding.standardcommon.imp.NoOpDownloadManager;
import net.buildabrowser.babbrowser.embedding.standardcommon.imp.NoOpTabManager;
import net.buildabrowser.babbrowser.embedding.standardcommon.net.imp.FetchBackendImp;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchPolicy;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.renderer.RenderingEngineBuilder;
import net.buildabrowser.babbrowser.renderer.content.input.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public final class StandardCommonEmbedding {
  
  private StandardCommonEmbedding() {}

  public static void configure(
    RenderingEngineBuilder builder
  ) {
    ContentEncodingRegistry registry = ContentEncodingRegistry.createDefault();
    ExecutorService httpExecutorService = Executors.newWorkStealingPool(16);
    FetchBackend fetchBackend = new FetchBackendImp(registry, httpExecutorService);
    DocumentLoaderRegistry loaderRegistry = DocumentLoaderRegistry.createDefault();
    builder
      .setFetchBackend(fetchBackend)
      .setFetchPolicy(new FetchPolicy() {})
      .setCookieStore(new InMemoryCookieStore(_1 -> false))
      .setUAChooser(new BasicUAChooserImp())
      .setDocumentLoaderRegistry(loaderRegistry)
      .setVirtualKeyboardFactory(_1 -> new VirtualKeyboard() {})
      .setThreadGroupSupplier(Executors::newVirtualThreadPerTaskExecutor)
      .setResourceResolver(StandardCommonEmbedding.class.getClassLoader()::getResourceAsStream)
      .setTabManager(new NoOpTabManager())
      .setDownloadManager(new NoOpDownloadManager());
  }

}
