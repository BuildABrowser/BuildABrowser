package net.buildabrowser.babbrowser.browser;

public final class BrowserVersion {
  
  public static final String NAME = "BuildABrowser Browser";
  public static final String NAME_SHORT = "BABBrowser";
  public static final int MAJOR_VERSION = 0;
  public static final int MINOR_VERSION = 1;
  public static final int PATCH_VERSION = 0;

  private BrowserVersion() {}

  public static String asVersionString() {
    return new StringBuilder(NAME)
      .append(" v")
      .append(MAJOR_VERSION)
      .append('.')
      .append(MINOR_VERSION)
      .append('.')
      .append(PATCH_VERSION)
      .toString();
  }

  public static String asUAString() {
    return new StringBuilder(NAME_SHORT)
      .append("/")
      .append(MAJOR_VERSION)
      .append('.')
      .append(MINOR_VERSION)
      .append('.')
      .append(PATCH_VERSION)
      .toString();
  }

}
