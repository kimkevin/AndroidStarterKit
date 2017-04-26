package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Module {
  @SerializedName("class")
  private List<String> names;

  @SerializedName("command")
  private List<String> commands;

  @SerializedName("java_config")
  private List<JavaConfig> javaConfigs;

  @SerializedName("gradle_config")
  private List<GradleConfig> gradleConfigs;

  public List<String> getClassNames() {
    return names;
  }

  public List<String> getCommands() {
    return commands;
  }

  public List<String> getNames() {
    return names;
  }

  public List<JavaConfig> getJavaConfigs() {
    return javaConfigs;
  }

  public List<GradleConfig> getGradleConfigs() {
    return gradleConfigs;
  }

  public boolean containCommand(String key) {
    return commands.contains(key);
  }

  @Override
  public String toString() {
    return "Module{" +
        "names=" + names +
        ", commands=" + commands +
        ", javaConfigs=" + javaConfigs +
        ", gradleConfigs=" + gradleConfigs +
        '}';
  }
}
