package net.buildabrowser.babbrowser.cssbase.property.shared;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record TwoSideValue(CSSValue first, CSSValue second) implements CSSValue {
  
  @Override
  public String serialize() {
    if (
      first != null
      && first.equals(second)
    ) {
      return first.serialize();
    } else {
      return CSSSerializerUtil.serializeManySpaces(first, second);
    }
  }
  
}
