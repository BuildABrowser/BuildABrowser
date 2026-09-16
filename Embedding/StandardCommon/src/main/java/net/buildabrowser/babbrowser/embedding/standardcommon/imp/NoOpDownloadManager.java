package net.buildabrowser.babbrowser.embedding.standardcommon.imp;

import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.html.ua.DownloadManager;

public class NoOpDownloadManager implements DownloadManager {

  @Override 
  public boolean allowDownload(FetchRequest request) {
    return false;
  }

  @Override 
  public boolean allowDownload(FetchResponse response) {
    return false;
  }

  @Override 
  public void startDownload(FetchResponse response, String suggestedFilename) {}
  
}
