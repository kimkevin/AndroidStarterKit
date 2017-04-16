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

  @SerializedName("group_config")
  private List<Config> groupConfigs;

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

  public List<Config> getGroupConfigs() {
    return groupConfigs;
  }

  public List<Module> getModules() {
    return modules;
  }
}
