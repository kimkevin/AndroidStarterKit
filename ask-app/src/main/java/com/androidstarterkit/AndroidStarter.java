package com.androidstarterkit;

public class AndroidStarter {
  private static boolean isUsedProgramArg = false;

  public static void main(String[] args) {
    String projectPath;

    CommandParser commandParser = new CommandParser(args);

    if (commandParser.hasHelpCommand()) {
      printHelp();
      return;
    }

    projectPath = FileUtils.linkPathWithSlash(FileUtils.getRootPath(), "AndroidSample");

    WidgetType type = WidgetType.RecyclerView;

    if (!isUsedProgramArg) {
      try {
        projectPath = commandParser.getPath();
        type = commandParser.getWidgetType();
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }

    switch (type) {
      case RecyclerView:
        Source
                .load(projectPath)
                .with(type)
                .put(FileNames.COFFEE_TYPE)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.RECYCLERVIEW_ADAPTER)
                .put(FileNames.ACTIVITY_RECYCLERVIEW_XML)
                .put(FileNames.LAYOUT_LIST_ITEM_XML);
        break;
      case ListView:
        Source
                .load(projectPath)
                .with(type)
                .put(FileNames.COFFEE_TYPE)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.LISTVIEW_ADAPTER)
                .put(FileNames.ACTIVITY_LISTVIEW_XML)
                .put(FileNames.LAYOUT_LIST_ITEM_XML);
        break;
      case SlidingTabLayout:
        Source
                .load(projectPath)
                .with(type)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.SLIDINGTAB_ADAPTER)
                .put(FileNames.SLIDINGTAB_FRAGMNET)
                .put(FileNames.SLIDINGTABLAYOUT)
                .put(FileNames.SLIDINGTABSTRIP)
                .put(FileNames.FRAGMNET_MAIN_XML)
                .put(FileNames.TAB_TEXT_LAYOUT_XML)
                .put(FileNames.ACTIVITY_SLIDINGTABLAYOUT_XML);
        break;
      case SlidingIconTabLayout:
        Source
                .load(projectPath)
                .with(type)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.SLIDINGICONTAB_ADAPTER)
                .put(FileNames.SLIDINGTAB_FRAGMNET)
                .put(FileNames.SLIDINGTABLAYOUT)
                .put(FileNames.SLIDINGTABSTRIP)
                .put(FileNames.FRAGMNET_MAIN_XML)
                .put(FileNames.TAB_IMAGE_LAYOUT_XML)
                .put(FileNames.ACTIVITY_SLIDINGTABLAYOUT_XML);
        break;
    }
  }

  public static void printHelp() {
    System.out.println();
    System.out.println("Usage: AndroidStater <options> <dir>");
    System.out.println();

    System.out.println("Options:");
    System.out.println();
    System.out.println("    -h, --help                  output usage information");
    System.out.println("    -w, --widget <view>         add <view> support (RecyclerView, ListView) (defaults to RecyclerView)");
    System.out.println();

    System.out.println("Dir:");
    System.out.println();
    System.out.println("    -p, --path                  source project path (defaults to new project)");
    System.out.println();
  }
}
