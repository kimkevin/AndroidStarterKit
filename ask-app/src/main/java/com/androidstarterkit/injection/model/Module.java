package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Module {
  @SerializedName("class")
  private List<String> names;

  @SerializedName("command")
  private List<String> commands;

  @SerializedName("config")
  private List<Config> configs;

  public List<String> getClassNames() {
    return names;
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
        "name='" + names + '\'' +
        ", commands=" + commands +
        ", configs=" + (configs != null ? configs.toString() : "") +
        '}';
  }
}
