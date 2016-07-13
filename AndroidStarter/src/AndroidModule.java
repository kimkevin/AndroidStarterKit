import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AndroidModule {
  public static String APPLICATION_ID = "com.kimkevin.module";
  public static String APP_MODULE_PATH = "AndroidModule/app";
  public static String CLASSES_PATH = "/bin/production/AndroidStarter";

  private Map<String, String> fileMap;

  private String homePath;

  public AndroidModule() {
    File rootPath = new File(".");
    try {
      homePath = rootPath.getCanonicalPath().replace(CLASSES_PATH, "");
    } catch (IOException e) {
      e.printStackTrace();
    }

    fileMap = new HashMap<>();
    fileMap.put(FileNames.ACTIVITY_MAIN_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.LAYOUT_LIST_ITEM_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.RECYCLERVIEW_ACTIVITY, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/kimkevin/module"));
    fileMap.put(FileNames.RECYCLERVIEW_ADAPTER, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "/src/main/java/com/kimkevin/module/adapter"));
    fileMap.put(FileNames.BUILD_GRADLE, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH));
  }

  public String getPath(String key) {
    return fileMap.get(key);
  }
}
