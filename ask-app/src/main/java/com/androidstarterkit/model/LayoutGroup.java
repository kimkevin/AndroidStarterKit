package com.androidstarterkit.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LayoutGroup {
  @SerializedName("class")
  private String name;

  @SerializedName("command")
  private List<String> commands;

  public String getClassName() {
    return name;
  }

  public List<String> getCommands() {
    return commands;
  }

  public boolean containCommand(String key) {
    return commands.contains(key);
  }

  @Override
  public String toString() {
    return "Layout{" +
        "name='" + name + '\'' +
        ", commands=" + commands +
        '}';
  }
}
