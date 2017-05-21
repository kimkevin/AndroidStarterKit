package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Snippet {
  @SerializedName("element")
  private List<String> elements;

  @SerializedName("lines")
  private List<String> codelines;

  public Snippet(List<String> elements, String codeline) {
    this.elements = elements;

    if (codelines == null) {
      codelines = new ArrayList<>();
    }
    codelines.add(codeline);
  }

  public Snippet(List<String> elements, List<String> codelines) {
    this.elements = elements;
    this.codelines = codelines;
  }

  public List<String> getElements() {
    if (elements == null) {
      elements = new ArrayList<>();
    }
    return elements;
  }

  public void setElements(List<String> elements) {
    this.elements = elements;
  }

  public List<String> getCodelines() {
    return codelines;
  }

  public void setCodelines(List<String> codelines) {
    this.codelines = codelines;
  }

  public boolean hasElement() {
    return elements != null;
  }

  @Override
  public String toString() {
    return "CodeBlock{" +
        "elements=" + (elements != null ? elements.toString() : "" ) +
        ", codelines=" + codelines.toString() +
        '}';
  }
}
