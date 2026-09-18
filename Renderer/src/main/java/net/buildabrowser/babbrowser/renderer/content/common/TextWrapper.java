package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.layout.FontWordCache;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.textshaping.core.LoadedFont;
import net.buildabrowser.babbrowser.textshaping.core.TextRun;
import net.buildabrowser.babbrowser.textshaping.core.TextRuns;

public final class TextWrapper {
  
  private TextWrapper() {}

  public static void layoutText(
    LayoutContext layoutContext,
    TextWrapTarget textWrapTarget,
    Text sourceText,
    String allText,
    boolean autoWrap
  ) {
    // TODO: Properly handle whitespace at line start/end, and break-word
    int textCursor = 0;
    while (textCursor < allText.length()) {
      int ch = allText.codePointAt(textCursor);

      if (textWrapTarget.ignoreWhitespace()) while (
        textCursor < allText.length()
        && (ch = allText.codePointAt(textCursor)) == ' '
        || ch == '\r'
      ) {
        textCursor++;
      }

      if (isForcedLineBreak(ch)) {
        textWrapTarget.nextLine(false);
        textCursor++;
        continue;
      }

      int startCursor = textCursor;

      while (
        textCursor < allText.length()
        && (
          textCursor == startCursor
          || (
            (ch = allText.codePointAt(textCursor)) != ' '
            && ch != '\r'
            && ch != '\u200B'))
        && !isForcedLineBreak(ch)
      ) {
        textCursor++;
      }

      boolean isForcedLineBreak = isForcedLineBreak(ch);
      if (textCursor < allText.length()) {
        textCursor++;
      }

      String selectedText = allText.substring(
        startCursor, textCursor + (isForcedLineBreak ? -1 : 0));
      addTextOrWrap(
        layoutContext,
        sourceText, selectedText, startCursor,
        textWrapTarget, autoWrap);
      
      if (isForcedLineBreak) {
        textWrapTarget.nextLine(false);
      }
    }
  }

  private static void addTextOrWrap(
    LayoutContext layoutContext,
    Text sourceText, String text, int sourceIndex,
    TextWrapTarget textWrapTarget, boolean autoWrap
  ) {
    LoadedFont font = layoutContext.font();
    FontWordCache wordCache = layoutContext.global().fontWordCache();
    TextRuns textRuns = wordCache.shapeWord(font, text);
    float textWidth = textRuns.runSize();
    float textHeight = font.metrics().height(); // TODO: Need to check against fallbacks

    boolean textOverflows = !textWrapTarget.fits(textWidth, true);
    boolean shouldWrap = autoWrap && textOverflows;
    if (shouldWrap) {
      // TODO: If a float was involved, drop down to the next point the text would fit post-float
      textWrapTarget.nextLine(true);
    }

    textWrapTarget.appendText(
      textRuns.runs(),
      sourceIndex, sourceIndex + text.length(),
      textWidth, textHeight);
  }

  private static boolean isForcedLineBreak(int codepoint) {
    return switch (codepoint) {
      case '\f', '\n', '\u000B', '\u2028', '\u2029', '\u0085' -> true;
      default -> false;
    };
  }

  public static interface TextWrapTarget {

    void nextLine(boolean isSoftWrap);

    // TODO: This is sufficient for trimming starting whitespace, but not tailing
    // whitespace. In the future, it may be necessary to make LineSegment store
    // child line segments instead of fragments, and do a post-run pass to alter
    // the text segments.
    boolean ignoreWhitespace();

    boolean fits(
      float itemSize, boolean forceFirst
    );

    void appendText(
      TextRun runs,
      int sourceStartIndex, int sourceEndIndex,
      float width, float height
    );

  }

}
