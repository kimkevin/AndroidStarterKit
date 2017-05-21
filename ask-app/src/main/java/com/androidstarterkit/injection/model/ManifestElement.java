package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ManifestElement {
  @SerializedName("node")
  private List<String> nodes;

  @SerializedName("attribute")
  private Map<String, String> attributes;

  public List<String> getNodes() {
    return nodes;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  @Override
  public String toString() {
    return "ManifestElement{" +
        "nodes=" + nodes +
        ", attributes=" + attributes +
        '}';
  }
}
