package net.buildabrowser.babbrowser.html.link;

import java.net.URI;
import java.util.UUID;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchClient;

public record LinkProcessingOptions(
  String href,
  String type,
  URI baseURL,
  FetchClient environment,
  Document document,
  // UA extension
  UUID relatedNavigableUUID
) {
  
}
