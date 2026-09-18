package net.buildabrowser.babbrowser.textshaping.core;

public record FontFamily(
  String name, boolean isGeneric
) {

  public static FontFamily MONOSPACE = new FontFamily("monospace", true);
  public static FontFamily SERIF = new FontFamily("serif", true);
  public static FontFamily SANS_SERIF = new FontFamily("sans-serif", true);

  public static  FontFamily named(String name) {
    return new FontFamily(name, false);
  }
  
}
