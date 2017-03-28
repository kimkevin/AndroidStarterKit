package com.androidstarterkit.command;

import com.androidstarterkit.model.CodeBlock;
import com.androidstarterkit.model.Config;
import com.androidstarterkit.model.Layout;
import com.androidstarterkit.model.Module;

import java.util.ArrayList;
import java.util.List;


public class AskJson {
  public static final String FILE_NAME = "ask.json";

  public static final String PROJECT_PATH_REPLACEMENT = "[root]";
  public static final String APP_PATH_REPLACEMENT = "[app]";
  public static final String JAVA_PATH_REPLACEMENT = "[java]";
  public static final String MAIN_ACTIVITY_REPLACEMENT = "[main]";

  private List<Layout> layouts;
  private List<Module> modules;

  private String projectPathname;
  private String appPathname;
  private String javaPathname;
  private String mainActivityName;

  public void replace(String projectPathname, String appPathname, String javaPathname, String mainActivityName) {
    this.projectPathname = projectPathname;
    this.appPathname = appPathname;
    this.javaPathname = javaPathname;
    this.mainActivityName = mainActivityName;

    for (Module module : modules) {
      for (Config config : module.getConfigs()) {
        config.setPath(replacedString(config.getPath()));
        config.setFileFullName(replacedString(config.getFileFullName()));

        for (CodeBlock block : config.getCodeBlocks()) {
          List<String> elements = new ArrayList<>();
          for (String element : block.getElements()) {
            elements.add(replacedString(element));
          }
          block.setElements(elements);
        }
      }
    }
  }

  private String replacedString(String originalText) {
    return originalText.replaceAll(PROJECT_PATH_REPLACEMENT, projectPathname)
        .replaceAll(APP_PATH_REPLACEMENT, appPathname)
        .replaceAll(JAVA_PATH_REPLACEMENT, javaPathname)
        .replaceAll(MAIN_ACTIVITY_REPLACEMENT, mainActivityName);
  }

  public List<Layout> getLayouts() {
    return layouts;
  }

  public List<Module> getModules() {
    return modules;
  }

  public Layout getLayoutClass(String command) {
    try {
      for (Layout layout : layouts) {
        if (layout.containCommand(command.toLowerCase())) {
          return layout;
        }
      }
    } catch (NullPointerException exception) {
      return null;
    }
    return null;
  }

  public Module getModuleClass(String command) {
    try {
      for (Module module : modules) {
        if (module.containCommand(command.toLowerCase())) {
          return module;
        }
      }
    } catch (NullPointerException exception) {
      return null;
    }
    return null;
  }

  @Override
  public String toString() {
    return "Command{" +
        "layouts=" + layouts +
        ", modules=" + modules +
        '}';
  }
}
