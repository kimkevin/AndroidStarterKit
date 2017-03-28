package com.androidstarterkit.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock {
  private List<String> elements;
  @SerializedName("lines")
  private List<String> codelines;

  public CodeBlock(List<String> elements, String codeline) {
    this.elements = elements;

    if (codelines == null) {
      codelines = new ArrayList<>();
    }
    codelines.add(codeline);
  }

  public CodeBlock(List<String> elements, List<String> codelines) {
    this.elements = elements;
    this.codelines = codelines;
  }

  public List<String> getElements() {
    return elements;
  }

  public void setElements(List<String> elements) {
    this.elements = elements;
  }

  public List<String> getCodelines() {
    return codelines;
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
