package com.androidstarterkit.injection.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AskJson {
  @SerializedName("layoutGroup")
  private List<LayoutGroup> layoutGroups;

  @SerializedName("moduleGroup")
  private List<ModuleGroup> moduleGroups;

  public List<LayoutGroup> getLayoutGroups() {
    return layoutGroups;
  }

  public List<ModuleGroup> getModuleGroups() {
    return moduleGroups;
  }

  public void append(AskJson askJson) {
    if (askJson.getLayoutGroups() != null) {
      if (layoutGroups == null) {
        layoutGroups = new ArrayList<>();
      }

      layoutGroups.addAll(askJson.getLayoutGroups());
    }

    if (askJson.getModuleGroups() != null) {
      if (moduleGroups == null) {
        moduleGroups = new ArrayList<>();
      }

      moduleGroups.addAll(askJson.getModuleGroups());
    }
  }

  public LayoutGroup getLayoutGroupByCommand(String command) {
    try {
      for (LayoutGroup layoutGroup : layoutGroups) {
        if (layoutGroup.containCommand(command.toLowerCase())) {
          return layoutGroup;
        }
      }
    } catch (NullPointerException exception) {
      return null;
    }
    return null;
  }

  public ModuleGroup getModuleGroupByCommand(String command) {
    try {
      for (ModuleGroup moduleGroup : moduleGroups) {
        for (Module module : moduleGroup.getModules()) {
          if (module.containCommand(command.toLowerCase())) {
            return moduleGroup;
          }
        }
      }
    } catch (NullPointerException exception) {
      return null;
    }
    return null;
  }

  public Module getModuleByCommand(String command) {
    try {
      for (Module module : getModuleGroupByCommand(command).getModules()) {
        if (module.containCommand(command.toLowerCase())) {
          return module;
        }
      }
    } catch (NullPointerException exception) {
      return null;
    }
    return null;
  }
}
