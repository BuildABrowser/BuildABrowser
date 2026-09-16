package net.buildabrowser.babbrowser.renderer.imp.html;

import java.util.concurrent.atomic.AtomicBoolean;

import net.buildabrowser.babbrowser.a11y.core.A11YCallbacks;
import net.buildabrowser.babbrowser.a11y.core.A11YFrame;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.renderer.logging.PerfLogging;

public class HTMLA11YFocusManagerCallbacks implements A11YCallbacks {
  
  private final AtomicBoolean queuedA11YTask = new AtomicBoolean();

  private final HTMLDocument document;
  private final A11YFrame a11yFrame;

  public HTMLA11YFocusManagerCallbacks(
    HTMLDocument nodeDocument,
    A11YFrame frame
  ) {
    this.document = nodeDocument;
    this.a11yFrame = frame;
  }

  @Override
  public void onNodeFocused(Node node) {
    if (node == null) return;

    boolean wasQueued = queuedA11YTask.getAndSet(true);
    if (!wasQueued) {
      EventLoop.queueGlobalTask(
        TaskSource.USER_INTERACTION,
        document.browsingContext().activeWindow(),
        this::runA11YTask);
    }
  }

  private void runA11YTask() {
    queuedA11YTask.set(false);
    long a11yStartTime = System.currentTimeMillis();
    a11yFrame.update(document);
    PerfLogging.logA11YTime(a11yStartTime);
  }

}
