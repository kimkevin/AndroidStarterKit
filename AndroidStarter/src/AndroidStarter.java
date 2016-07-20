
public class AndroidStarter {
  private static boolean isUsedProgramArg = false;

  public static void main(String[] args) {
    String projectPath;

    CommandParser commandParser = new CommandParser(args);

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
                .put(FileNames.RECYCLERVIEW_ADAPTER)
                .put(FileNames.ACTIVITY_RECYCLERVIEW_XML)
                .put(FileNames.LAYOUT_LIST_ITEM_XML);
        break;
      case SlidingIconTabLayout:
        Source
                .load(projectPath)
                .with(type)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.RECYCLERVIEW_ADAPTER)
                .put(FileNames.ACTIVITY_RECYCLERVIEW_XML)
                .put(FileNames.LAYOUT_LIST_ITEM_XML);
        break;
    }
  }
}
