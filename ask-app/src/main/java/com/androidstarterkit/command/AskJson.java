package com.androidstarterkit.command;

import com.androidstarterkit.model.CodeBlock;
import com.androidstarterkit.model.Config;
import com.androidstarterkit.model.LayoutGroup;
import com.androidstarterkit.model.Module;
import com.androidstarterkit.model.ModuleGroup;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class AskJson {
  public static final String FILE_NAME = "ask.json";

  public static final String PROJECT_PATH_REPLACEMENT = "[root]";
  public static final String APP_PATH_REPLACEMENT = "[app]";
  public static final String JAVA_PATH_REPLACEMENT = "[java]";
  public static final String MAIN_ACTIVITY_REPLACEMENT = "[main]";
  public static final String PACKAGE_REPLACEMENT = "[package]";

  @SerializedName("layoutGroup")
  private List<LayoutGroup> layoutGroups;

  @SerializedName("moduleGroup")
  private List<ModuleGroup> moduleGroups;

  private String projectPathname;
  private String appPathname;
  private String javaPathname;
  private String mainActivityName;
  private String applicationId;

  public void replace(String projectPathname, String appPathname, String javaPathname, String mainActivityName, String applicationId) {
    this.projectPathname = projectPathname;
    this.appPathname = appPathname;
    this.javaPathname = javaPathname;
    this.mainActivityName = mainActivityName;
    this.applicationId = applicationId;

    for (ModuleGroup moduleGroup : moduleGroups) {
      for (Config config : moduleGroup.getGroupConfigs()) {
        replaceConfig(config);
      }

      for (Module module : moduleGroup.getModules()) {
        for (Config config : module.getConfigs()) {
          replaceConfig(config);
        }
      }
    }
  }

  private void replaceConfig(Config config) {
    config.setPath(replacedString(config.getPath()));
    config.setFileNameEx(replacedString(config.getFileNameEx()));

    for (CodeBlock block : config.getCodeBlocks()) {
      if (block.getElements() != null) {
        List<String> elements = new ArrayList<>();
        for (String element : block.getElements()) {
          elements.add(replacedString(element));
        }

        block.setElements(elements);
      }

      if (block.getCodelines() != null) {
        List<String> codelines = new ArrayList<>();
        for (String codeline : block.getCodelines()) {
          codelines.add(replacedString(codeline));
        }

        block.setCodelines(codelines);
      }
    }
  }

  private String replacedString(String originalText) {
    return originalText.replaceAll(PROJECT_PATH_REPLACEMENT, projectPathname)
        .replaceAll(APP_PATH_REPLACEMENT, appPathname)
        .replaceAll(JAVA_PATH_REPLACEMENT, javaPathname)
        .replaceAll(MAIN_ACTIVITY_REPLACEMENT, mainActivityName)
        .replaceAll(PACKAGE_REPLACEMENT, applicationId);
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
