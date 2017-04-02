package com.androidstarterkit.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModuleGroup {
  @SerializedName("name")
  private String name;

  @SerializedName("config_files")
  private List<String> configFilenames;

  @SerializedName("page")
  private String page;

  @SerializedName("group_configs")
  private List<Config> groupConfigs;

  @SerializedName("modules")
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

  public List<Config> getGroupConfigs() {
    return groupConfigs;
  }

  public List<Module> getModules() {
    return modules;
  }
}
