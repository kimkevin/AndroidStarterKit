package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ManifestConfig extends Config {
  @SerializedName("element")
  private List<ManifestElement> manifestElements;

  public ManifestConfig(String path, String fileNameEx) {
    super(path, fileNameEx);
  }

  public List<ManifestElement> getManifestElements() {
    return manifestElements;
  }

  @Override
  public String toString() {
    return "ManifestConfig{" +
        "manifestElements=" + manifestElements +
        '}';
  }
}
