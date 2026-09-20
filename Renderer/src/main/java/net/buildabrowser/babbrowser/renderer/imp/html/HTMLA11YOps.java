package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.a11y.core.A11YOps;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;

public class HTMLA11YOps implements A11YOps {

  private final EventContext eventContext;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public HTMLA11YOps(
    EventContext eventContext,
    SlotFamily<HTMLElement, RenderContext> elementContexts
  ) {
    this.eventContext = eventContext;
    this.renderContexts = elementContexts;
  }

  @Override
  public boolean isSkipped(Node node) {
    if (!(
      node instanceof HTMLElement htmlElement
    )) return false;

    RenderContext context = renderContexts.get(htmlElement);
    return
      context.properties() != null
      && PropertiesUtil.outerDisplayValue(context.properties())
        .equals(OuterDisplayValue.NONE);
  }

  @Override
  public boolean isIgnored(Node node) {
    if (!(
      node instanceof HTMLElement htmlElement
    )) return false;

    RenderContext context = renderContexts.get(htmlElement);
    if (context.properties() == null) return false;
    return PropertiesUtil.outerDisplayValue(context.properties())
      .equals(OuterDisplayValue.CONTENTS);
  }

  @Override
  public boolean hasSemanticMeaning(Element element) {
    if (!(
      element instanceof HTMLElement htmlElement
    )) return false;

    RenderContext context = renderContexts.get(htmlElement);
    if (context.properties() == null) return false;
    return
      !context.properties().get(CSSProperty.OVERFLOW_X).equals(OverflowValue.VISIBLE)
      || !context.properties().get(CSSProperty.OVERFLOW_Y).equals(OverflowValue.VISIBLE);
  }

  @Override
  public boolean isActivatable(Node node) {
    return
      node instanceof Element element
      && element instanceof ActivationTarget
      && !element.hasAttribute("disabled");
  }

  @Override
  public void activate(Node node) {
    if (!isActivatable(node)) return;
    Event event = PointerEvent.createGeneric("click");
    EventUtil.forwardElementEvent(eventContext, event, (Element) node);
  }
  
}
