package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public final class FlowLayerPositioning {
  
  private FlowLayerPositioning() {}

  public static void positionLayers(
    float layerX, float layerY,
    ManagedBoxFragment<?> rootFragment,
    List<BoxFragment<?>> floats
  ) {
    rootFragment.setLayerPos(layerX, layerY);

    ElementBox rootBox = rootFragment.box();
    recursePositionLayers(
      layerX, layerY, 0, 0,
      rootFragment, rootBox.stackingContext());

    float rootContentX = layerX + (rootFragment.posX(Measurement.CONTENT) - rootFragment.posX(Measurement.BORDER));
    float rootContentY = layerY + (rootFragment.posY(Measurement.CONTENT) - rootFragment.posY(Measurement.BORDER));

    for (BoxFragment<?> floatFragment : floats) {
      positionFloatLayers(
        rootContentX + floatFragment.layerX(Measurement.BORDER),
        rootContentY + floatFragment.layerY(Measurement.BORDER),
        (UnmanagedBoxFragment<?>) floatFragment, rootBox.stackingContext());
    }
  }

  private static void recursePositionLayers(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    LayoutFragment fragment, StackingContext refContext
  ) {
    fragment.setLayerPos(layerX, layerY);
    switch (fragment) {
      case TextFragment textFragment -> textFragment.setLayerPos(layerX, layerY);
      case PosRefBoxFragment posRef -> {
        posRef.box().alterDimensions(false, d -> d.setStaticPosition(layerX, layerY));
      }
      case LineBoxFragment lineBoxFragment -> recursePositionLineBoxFragment(
        layerX, layerY, layerStartX, layerStartY, refContext, lineBoxFragment);
      case ManagedBoxFragment<?> boxFragment -> recursePositionManagedBoxFragment(
        layerX, layerY, layerStartX, layerStartY, refContext, boxFragment);
      case UnmanagedBoxFragment<?> boxFragment -> recursePositionUnmanagedBoxFragment(
        layerX, layerY, refContext, boxFragment);
      case FloatRefFragment floatRefFragment -> floatRefFragment.setFloatLayerStartPos(
        layerStartX, layerStartY);

      default -> throw new UnsupportedOperationException("Don't recognize fragment type!");
    }
  }

  private static void recursePositionLineBoxFragment(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    StackingContext refContext, LineBoxFragment lineBoxFragment
  ) {
    LayoutFragment child = lineBoxFragment.fragments();
    while (child != null) {
      recursePositionLayers(
        layerX + child.posX(Measurement.BORDER),
        layerY + child.posY(Measurement.BORDER),
        layerStartX, layerStartY,
        child, refContext);
      child = child.next();
    }
  }

  private static void recursePositionManagedBoxFragment(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    StackingContext refContext,
    ManagedBoxFragment<?> boxFragment
  ) {
    if (boxFragment.box().stackingContext() != refContext) {
      boxFragment.box().stackingContext().positionFragment(
        layerX, layerY, boxFragment,
        (f, childLayerX, childLayerY) -> recursePositionManagedBoxFragmentInner(
          childLayerX, childLayerY,
          layerStartX + layerX - childLayerX,
          layerStartY + layerY - childLayerY,
          f));
    } else {
      recursePositionManagedBoxFragmentInner(
        layerX, layerY,
        layerStartX, layerStartY,
        boxFragment);
    }
  }

  private static void recursePositionManagedBoxFragmentInner(
    float layerX, float layerY,
    float layerStartX, float layerStartY,
    ManagedBoxFragment<?> fragment
  ) {
    float offsetX = layerX + (fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER));
    float offsetY = layerY + (fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER));
    
    LayoutFragment child = fragment.fragments();
    while (child != null) {
      recursePositionLayers(
        offsetX + child.posX(Measurement.BORDER),
        offsetY + child.posY(Measurement.BORDER),
        layerStartX, layerStartY,
        child, fragment.box().stackingContext());
      child = child.next();
    }
  }

  private static void recursePositionUnmanagedBoxFragment(
    float layerX, float layerY,
    StackingContext refContext,
    UnmanagedBoxFragment<?> boxFragment
  ) {
    ElementBox box = boxFragment.box();
    if (box.stackingContext() != refContext) {
      box.stackingContext().positionFragment(
        layerX, layerY, boxFragment,
        (frag, innerX, innerY) -> box.content().positionLayers(frag, innerX, innerY));
    } else {
      box.content().positionLayers(boxFragment, layerX, layerY);
    }
  }

  // TODO: Ensure this logic is correct. It's really hacky, just tuned it until it seemed to work
  // If you nest an overflow container in a float in a relative box, this needs to handle that
  // But changes should also not cause regressions on Acid2
  private static void positionFloatLayers(
    float layerX, float layerY,
    UnmanagedBoxFragment<?> boxFragment,
    StackingContext refContext
  ) {
    ElementBox box = boxFragment.box();
    if (box.stackingContext() == null) {
      assert false;
    } else if (box.stackingContext() != refContext) {
      box.stackingContext().positionNormalizedFragment(
        layerX, layerY, boxFragment,
        (frag, innerX, innerY) -> {
          float innerContentX = innerX + (frag.posX(Measurement.CONTENT) - frag.posX(Measurement.BORDER));
          float innerContentY = innerY + (frag.posY(Measurement.CONTENT) - frag.posY(Measurement.BORDER));
          box.content().positionLayers(frag, innerContentX, innerContentY);
        });
    } else {
      boxFragment.setLayerPos(layerX, layerY);
      float contentX = layerX + (boxFragment.posX(Measurement.CONTENT) - boxFragment.posX(Measurement.BORDER));
      float contentY = layerY + (boxFragment.posY(Measurement.CONTENT) - boxFragment.posY(Measurement.BORDER));
      box.content().positionLayers(boxFragment, contentX, contentY);
    }
  }

}
