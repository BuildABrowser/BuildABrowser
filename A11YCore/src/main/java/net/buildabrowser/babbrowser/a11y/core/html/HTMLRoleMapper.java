package net.buildabrowser.babbrowser.a11y.core.html;

import net.buildabrowser.babbrowser.a11y.core.aom.AriaRole;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.infra.Namespace;

public final class HTMLRoleMapper {
  
  private HTMLRoleMapper() {}

  public static AriaRole mapNode(Node node) {
    if (node instanceof Document) {
      return AriaRole.DOCUMENT;
    } else if (node instanceof Text) {
      return AriaRole.STATICTEXT;
    }

    // TODO: What is the proper role to return in cases where currently none
    // is returned? AriaRole.GENERIC?
    if (!(
      node instanceof Element element
      && element.namespace().equals(Namespace.HTML_NAMESPACE)
    )) return null;

    String roleAttr = element.getAttribute("role");
    AriaRole explicitRole = mapRole(roleAttr);
    if (explicitRole != null) return explicitRole;

    // TODO: Check the element's aria-role attribute
    return switch (element.name()) {
      case "a" -> element.hasAttribute("href") ? AriaRole.LINK : AriaRole.GENERIC;
      case "abbr" -> null;
      case "address" -> AriaRole.GROUP;
      case "area" -> element.hasAttribute("href") ? AriaRole.LINK : AriaRole.GENERIC;
      case "article" -> AriaRole.ARTICLE;
      // TODO: Check the scope
      case "aside" -> AriaRole.COMPLEMENTARY;
      case "audio" -> null;
      // TODO: Custom element
      case "b" -> AriaRole.GENERIC;
      case "base" -> null;
      case "bdi" -> AriaRole.GENERIC;
      case "bdo" -> AriaRole.GENERIC;
      case "blockquote" -> AriaRole.BLOCKQUOTE;
      case "body" -> AriaRole.GENERIC;
      case "br" -> null;
      case "button" -> AriaRole.BUTTON;
      case "canvas" -> null;
      case "caption" -> AriaRole.CAPTION;
      case "cite" -> null;
      case "code" -> AriaRole.CODE;
      case "col" -> null;
      case "colgroup" -> null;
      case "data" -> AriaRole.GENERIC;
      // TODO: Also need to handle multiselectable
      case "datalist" -> AriaRole.LISTBOX;
      case "dd" -> AriaRole.DEFINITION;
      case "del" -> AriaRole.DELETION;
      case "details" -> AriaRole.GROUP;
      case "dfn" -> AriaRole.TERM;
      case "dialog" -> AriaRole.DIALOG;
      case "dir" -> AriaRole.LIST;
      case "div" -> AriaRole.GENERIC;
      case "dl" -> AriaRole.LIST;
      case "dt" -> AriaRole.TERM;
      case "em" -> AriaRole.EMPHASIS;
      case "embed" -> null;
      case "fieldset" -> AriaRole.GROUP;
      case "figcaption" -> AriaRole.CAPTION;
      case "figure" -> AriaRole.FIGURE;
      // TODO: Check footer scope
      case "footer" -> AriaRole.CONTENTINFO;
      case "form" -> AriaRole.FORM;
      // TODO: Form-associated custom element
      // TODO: Set the aria-level property
      case "h1", "h2", "h3", "h4", "h5", "h6" -> AriaRole.HEADING;
      case "head" -> null;
      // TODO: Check scope
      case "header" -> AriaRole.BANNER;
      case "hgroup" -> AriaRole.GROUP;
      case "hr" -> AriaRole.SEPARATOR;
      case "html" -> AriaRole.GENERIC;
      case "i" -> AriaRole.GENERIC;
      case "iframe" -> null;
      case "img" ->
        !element.hasAttribute("alt") ? AriaRole.PRESENTATION :
        element.getAttribute("alt").trim().equals("") ? AriaRole.PRESENTATION :
        AriaRole.IMAGE;
      case "input" -> inputRole(element);
      case "ins" -> AriaRole.INSERTION;
      case "kbd" -> null;
      case "label" -> null;
      case "legend" -> null;
      case "li" -> AriaRole.LISTITEM;
      case "link" -> null;
      case "main" -> AriaRole.MAIN;
      case "map" -> null;
      case "mark" -> AriaRole.MARK;
      // TODO: MathML math
      case "menu" -> AriaRole.MENU;
      case "meta" -> null;
      case "meter" -> AriaRole.METER;
      case "nav" -> AriaRole.NAVIGATION;
      case "noscript" -> null;
      case "object" -> null;
      case "ol" -> AriaRole.LIST;
      case "optgroup" -> AriaRole.GROUP;
      // TODO: aria-selected attribute
      case "option" -> AriaRole.OPTION;
      case "output" -> AriaRole.STATUS;
      case "p" -> AriaRole.PARAGRAPH;
      case "param" -> null;
      case "picture" -> null;
      case "pre" -> AriaRole.GENERIC;
      // TODO: Progressbar properties
      case "progress" -> AriaRole.PROGRESSBAR;
      case "q" -> AriaRole.GENERIC;
      case "rp" -> null;
      case "rt" -> null;
      case "ruby" -> null;
      case "s" -> AriaRole.DELETION;
      case "samp" -> AriaRole.GENERIC;
      case "script" -> null;
      case "search" -> AriaRole.SEARCH;
      // TODO: Check if there is an accessible name
      case "section" -> AriaRole.REGION;
      // TODO: Check select rendering
      case "select" -> AriaRole.LISTBOX;
      case "slot" -> null;
      case "small" -> AriaRole.GENERIC;
      case "source" -> null;
      case "span" -> AriaRole.GENERIC;
      case "strong" -> AriaRole.STRONG;
      case "style" -> null;
      case "sub" -> AriaRole.SUBSCRIPT;
      case "summary" -> null;
      case "sup" -> AriaRole.SUPERSCRIPT;
      // TODO: SVG image
      case "table" -> AriaRole.TABLE;
      case "tbody" -> AriaRole.ROWGROUP;
      // TODO: Check if in a grid
      case "td" -> AriaRole.CELL;
      case "template" -> null;
      // TODO: aria-multiline attribute
      case "textarea" -> AriaRole.TEXTBOX;
      case "tfoot" -> AriaRole.ROWGROUP;
      // TODO: Check if it really is a header
      case "th" -> AriaRole.COLUMNHEADER;
      case "thead" -> AriaRole.ROWGROUP;
      case "time" -> AriaRole.TIME;
      case "title" -> null;
      case "tr" -> AriaRole.ROW;
      case "track" -> null;
      case "u" -> AriaRole.GENERIC;
      case "ul" -> AriaRole.LIST;
      case "var" -> null;
      case "video" -> null;
      case "wbr" -> null;
      default -> null;
    };
  }

  // TODO: Check if there is a suggestion element
  private static AriaRole inputRole(Element element) {
    if (!element.hasAttribute("alt")) return AriaRole.TEXTBOX;
    return switch (element.getAttribute("alt")) {
      case "button" -> AriaRole.BUTTON;
      // TODO: Set aria-checked
      case "checkbox" -> AriaRole.CHECKBOX;
      case "color" -> null;
      case "date" -> null;
      case "datetime-local" -> null;
      case "email" -> AriaRole.TEXTBOX;
      case "file" -> null;
      case "hidden" -> null;
      case "image" -> AriaRole.BUTTON;
      case "month" -> null;
      case "number" -> AriaRole.SPINBUTTON;
      case "password" -> null;
      // TODO: Set checkedness
      case "radio" -> AriaRole.RADIO;
      case "range" -> AriaRole.SLIDER;
      case "reset" -> AriaRole.BUTTON;
      case "search" -> AriaRole.SEARCHBOX;
      case "submit" -> AriaRole.BUTTON;
      case "telephone" -> AriaRole.TEXTBOX;
      case "text" -> AriaRole.TEXTBOX;
      // TODO: Suggestions case
      case "time" -> null;
      case "url" -> AriaRole.TEXTBOX;
      case "week" -> null;
      default -> AriaRole.TEXTBOX;
    };
  }

  private static AriaRole mapRole(String role) {
    if (role == null) return null;

    return switch (role.trim().toLowerCase()) {
      case "alert" -> AriaRole.ALERT;
      case "alertdialog" -> AriaRole.ALERTDIALOGUE; // Maps to ALERTDIALOGUE in the enum
      case "application" -> AriaRole.APPLICATION;
      case "article" -> AriaRole.ARTICLE;
      case "banner" -> AriaRole.BANNER;
      case "button" -> AriaRole.BUTTON;
      case "cell" -> AriaRole.CELL;
      case "checkbox" -> AriaRole.CHECKBOX;
      case "columnheader" -> AriaRole.COLUMNHEADER;
      case "combobox" -> AriaRole.COMBOBOX;
      case "command" -> null; // TODO
      case "comment" -> AriaRole.COMMENT;
      case "complementary" -> AriaRole.COMPLEMENTARY;
      case "composite" -> null; // TODO
      case "contentinfo" -> AriaRole.CONTENTINFO;
      case "definition" -> AriaRole.DEFINITION;
      case "dialog" -> AriaRole.DIALOG;
      case "directory" -> AriaRole.DIRECTORY;
      case "document" -> AriaRole.DOCUMENT;
      case "feed" -> AriaRole.FEED;
      case "figure" -> AriaRole.FIGURE;
      case "form" -> AriaRole.FORM;
      case "generic" -> AriaRole.GENERIC;
      case "grid" -> AriaRole.GRID;
      case "gridcell" -> AriaRole.GRIDCELL;
      case "group" -> AriaRole.GROUP;
      case "heading" -> AriaRole.HEADING;
      case "img" -> AriaRole.IMG;
      case "input" -> AriaRole.TEXTBOX; // TODO: Type of input
      case "landmark" -> null; // TODO
      case "link" -> AriaRole.LINK;
      case "list" -> AriaRole.LIST;
      case "listbox" -> AriaRole.LISTBOX;
      case "listitem" -> AriaRole.LISTITEM;
      case "log" -> AriaRole.LOG;
      case "main" -> AriaRole.MAIN;
      case "mark" -> AriaRole.MARK;
      case "marquee" -> AriaRole.MARQUEE;
      case "math" -> AriaRole.MATH;
      case "menu" -> AriaRole.MENU;
      case "menubar" -> AriaRole.MENUBAR;
      case "menuitem" -> AriaRole.MENUITEM;
      case "menuitemcheckbox" -> AriaRole.MENUITEMCHECKBOX;
      case "menuitemradio" -> AriaRole.MENUITEMRADIO;
      case "meter" -> AriaRole.METER;
      case "navigation" -> AriaRole.NAVIGATION;
      case "none" -> AriaRole.NONE;
      case "note" -> AriaRole.NOTE;
      case "option" -> AriaRole.OPTION;
      case "presentation" -> AriaRole.PRESENTATION;
      case "progressbar" -> AriaRole.PROGRESSBAR;
      case "radio" -> AriaRole.RADIO;
      case "radiogroup" -> AriaRole.RADIOGROUP;
      case "range" -> null; // TODO
      case "region" -> AriaRole.REGION;
      case "roletype" -> null; // TODO
      case "row" -> AriaRole.ROW;
      case "rowgroup" -> AriaRole.ROWGROUP;
      case "rowheader" -> AriaRole.ROWHEADER;
      case "scrollbar" -> AriaRole.SCROLLBAR;
      case "search" -> AriaRole.SEARCH;
      case "searchbox" -> AriaRole.SEARCHBOX;
      case "section" -> AriaRole.SECTION;
      case "sectionhead" -> AriaRole.SECTIONHEADER;
      case "select" -> AriaRole.SELECT;
      case "separator" -> AriaRole.SEPARATOR;
      case "slider" -> AriaRole.SLIDER;
      case "spinbutton" -> AriaRole.SPINBUTTON;
      case "status" -> AriaRole.STATUS;
      case "structural", "structure" -> null;
      case "suggestion" -> AriaRole.SUGGESTION;
      case "switch" -> AriaRole.SWITCH;
      case "tab" -> AriaRole.TAB;
      case "table" -> AriaRole.TABLE;
      case "tablist" -> AriaRole.TABLIST;
      case "tabpanel" -> AriaRole.TABPANEL;
      case "term" -> AriaRole.TERM;
      case "textbox" -> AriaRole.TEXTBOX;
      case "timer" -> AriaRole.TIMER;
      case "toolbar" -> AriaRole.TOOLBAR;
      case "tooltip" -> AriaRole.TOOLTIP;
      case "tree" -> AriaRole.TREE;
      case "treegrid" -> AriaRole.TREEGRID;
      case "treeitem" -> AriaRole.TREEITEM;
      case "widget" -> null;
      case "window" -> AriaRole.WINDOW; // TODO
      default -> null;
    };
  }

}
