package com.androidstarterkit.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Module {
  @SerializedName("class")
  private String name;

  @SerializedName("commands")
  private List<String> commands;

  @SerializedName("configs")
  private List<Config> configs;

  public String getName() {
    return name;
  }

  public List<String> getCommands() {
    return commands;
  }

  public List<Config> getConfigs() {
    return configs;
  }

  public boolean containCommand(String key) {
    return commands.contains(key);
  }

  @Override
  public String toString() {
    return "Module{" +
        "name='" + name + '\'' +
        ", commands=" + commands +
        ", configs=" + (configs != null ? configs.toString() : "") +
        '}';
  }
}
