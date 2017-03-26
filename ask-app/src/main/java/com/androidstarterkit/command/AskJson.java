package com.androidstarterkit.command;

import com.androidstarterkit.model.Layout;
import com.androidstarterkit.model.Module;

import java.util.List;


public class AskJson {
  public static final String FILE_NAME = "ask.json";

  private List<Layout> layouts;
  private List<Module> modules;

  public Layout getLayoutClass(String command) {
    try {
      for (Layout layout : layouts) {
        if (layout.containCommand(command)) {
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
        if (module.containCommand(command)) {
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
