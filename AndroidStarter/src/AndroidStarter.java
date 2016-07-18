
public class AndroidStarter {
  private static boolean isDebuggable = true;

  public static void main(String[] args) {
    String projectPath;

    /**
     * Get project path and options through arguments
     */
    if (!isDebuggable && args.length <= 0) {
      System.out.println("Missed home path of your project");
      return;
    }

    if (isDebuggable) {
      projectPath = "/Users/kevin/Documents/git/AndroidStarterKit/AndroidSample";
    } else {
      projectPath = args[0];
    }

    WidgetType type = WidgetType.RecyclerView;

    switch (type) {
      case RecyclerView:
        Source
                .load(projectPath)
                .with(WidgetType.RecyclerView)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.RECYCLERVIEW_ADAPTER)
                .put(FileNames.ACTIVITY_MAIN_XML)
                .put(FileNames.LAYOUT_LIST_ITEM_XML);
        break;
    }
  }
}
