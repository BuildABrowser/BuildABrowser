package net.buildabrowser.babbrowser.a11y.core.noop;

import java.io.IOException;

import net.buildabrowser.babbrowser.a11y.core.A11YFrame;
import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.a11y.core.A11YProvider;

// TODO: Better to make Optional<A11YProvider> if this grows?
public class NoOpA11YProvider implements A11YProvider {

  @Override
  public A11YFrame createFrame(A11YOps ops) throws IOException {
    return new NoOpA11YFrame();
  }

  @Override
  public void initialize() {}
  
}
