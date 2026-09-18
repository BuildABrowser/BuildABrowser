package net.buildabrowser.babbrowser.renderer.layout.stacking;

import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerOptions;

public interface LayerGenerator<T> {

  T createLayer(
    StackingContextPosition position,
    CompositeLayerOptions compositeLayerOptions,
    int zIndexOrder,
    StackingContextEntry entries
  );

  void addChild(
    T layer,
    T child
  );
  
}
