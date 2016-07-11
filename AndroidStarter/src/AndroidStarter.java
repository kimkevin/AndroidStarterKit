
public class AndroidStarter {
  public static void main(String[] args) {
    /**
     * Get project path through arguments and module type
     */
    if (args.length <= 0) {
      System.out.println("Missed home path of your project");
      return;
    }

    String YOUR_PROJECT_HOME_PATH = args[0];
//    String YOUR_PROJECT_HOME_PATH = "/Users/kevin/Documents/git/AndroidStarterKit/AndroidSample";
    ModuleType type = ModuleType.RecyclerViewActivity;

    switch (type) {
      case RecyclerViewActivity:
        Source
                .load(YOUR_PROJECT_HOME_PATH)
                .with(ModuleType.RecyclerViewActivity)
                .put(FileNames.BUILD_GRADLE)
                .put(FileNames.RECYCLERVIEWADAPTER)
                .put(FileNames.ACTIVITY_MAIN_XML)
                .put(FileNames.LAYOUT_LIST_ITEM_XML);
        break;
    }
  }
}
