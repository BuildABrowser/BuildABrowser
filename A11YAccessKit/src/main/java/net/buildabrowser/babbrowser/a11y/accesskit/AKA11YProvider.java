package net.buildabrowser.babbrowser.a11y.accesskit;

import java.io.IOException;
import java.lang.foreign.MemorySegment;

import net.buildabrowser.ak4j.AK4J;
import net.buildabrowser.ak4j.AK4JHandle;
import net.buildabrowser.ak4j.AKCallbacks;
import net.buildabrowser.babbrowser.a11y.core.A11YFrame;
import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.a11y.core.A11YProvider;

public class AKA11YProvider implements A11YProvider {

  @Override
  public A11YFrame createFrame(A11YOps ops) throws IOException {
    return new AKA11YFrame(ops);
  }

  @Override
  public void initialize() throws Exception {
    // Force load, this is used to test if it works properly
    AK4J.init(new AKCallbacks() {
      @Override
      public MemorySegment onActivation(AK4JHandle ak4jHandle) {
        return AKA11YFrame.createFakeUpdate(ak4jHandle);
      }
    }).close();
  }
  
}
