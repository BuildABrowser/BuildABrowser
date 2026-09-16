package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;

public class TwoSideShorthandParser implements PropertyValueParser {

  private final PropertyValueParser innerParser;
  private final CSSProperty[] relatedProperties;
  private final CSSProperty primaryProperty;

  public TwoSideShorthandParser(
    PropertyValueParser innerParser,
    CSSProperty[] relatedProperties,
    CSSProperty primaryProperty
  ) {
    assert relatedProperties.length == 2;
    this.innerParser = innerParser;
    this.relatedProperties = relatedProperties;
    this.primaryProperty = primaryProperty;
  }

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    CSSValue firstValue = innerParser.parse(stream);
    if (firstValue.isFailure()) return firstValue;

    int mark = stream.mark();
    CSSValue secondValue = innerParser.parse(stream);
    if (!secondValue.isFailure()) {
      stream.discardMark();
      return new TwoSideValue(firstValue, secondValue);
    }
    stream.restoreMark(mark);

    return new TwoSideValue(firstValue, firstValue);
  }
  
  @Override
  public CSSProperty relatedProperty() {
    return this.primaryProperty;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    if (!(result instanceof TwoSideValue sides)) return;
    propertySetter.setProperty(relatedProperties[0], sides.first());
    propertySetter.setProperty(relatedProperties[1], sides.second());
  }

}
