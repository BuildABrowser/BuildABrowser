package net.buildabrowser.babbrowser.textshaping.core;

public interface FontMetrics {

  float size();

  int weight();

  float height();

  float xHeight();

  float ascent();

  float descent();
  
  float stringWidth(String text);

}
