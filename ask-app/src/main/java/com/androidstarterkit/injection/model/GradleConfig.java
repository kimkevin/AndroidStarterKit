package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GradleConfig extends Config {
  @SerializedName("snippet")
  private List<Snippet> snippets;

  public GradleConfig(String path, String fileNameEx) {
    this(path, fileNameEx, null);
  }

  private GradleConfig(String path, String fileNameEx, List<Snippet> snippets) {
    super(path, fileNameEx);
    this.snippets = snippets;
  }

  public List<Snippet> getSnippets() {
    return snippets;
  }

  public void addSnippet(Snippet snippet) {
    if (snippets == null) {
      snippets = new ArrayList<>();
    }

    snippets.add(snippet);
  }

  @Override
  public String toString() {
    return super.toString() + ", GradleConfig{" +
        "path='" + path + '\'' +
        ", fileNameEx='" + fileNameEx + '\'' +
        ", configs=" + snippets +
        '}';
  }
}
