package net.buildabrowser.babbrowser.html.util;

import java.net.URI;
import java.util.UUID;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public final class HTMLFetchUtil {
  
  private HTMLFetchUtil() {}

	public static MutableFetchRequest createPotentialCORSRequest(URI url) {
		MutableFetchRequest request = FetchRequest.createMutable();
		request.appendURL(url);
		return request;
	}

	public static UUID relatedUUID(Document document) {
		if (document == null) return null;
		if (!(document instanceof HTMLDocument htmlDocument)) return null;
		Navigable navigable = htmlDocument.nodeNavigable();
		if (navigable == null) return null;
		return navigable.uuid();
	}

}
