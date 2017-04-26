package com.androidstarterkit.injection.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModuleGroup {
  @SerializedName("name")
  private String name;

  @SerializedName("config_file")
  private List<String> configFilenames;

  @SerializedName("page")
  private String page;

  @SerializedName("group_gradle_config")
  private List<GradleConfig> groupGradleConfigs;

  @SerializedName("module")
  private List<Module> modules;

  public String getName() {
    return name;
  }

  public List<String> getConfigFilenames() {
    return configFilenames;
  }

  public String getPage() {
    return page;
  }

  public List<GradleConfig> getGroupGradleConfigs() {
    return groupGradleConfigs;
  }

  public List<Module> getModules() {
    return modules;
  }

  @Override
  public String toString() {
    return "ModuleGroup{" +
        "name='" + name + '\'' +
        ", configFilenames=" + configFilenames +
        ", page='" + page + '\'' +
        ", groupGradleConfigs=" + groupGradleConfigs +
        ", modules=" + modules +
        '}';
  }
}
