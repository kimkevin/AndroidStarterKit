import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AndroidModule {
  private Map<String, String> fileMap;

  private String homePath;

  public AndroidModule() {
    File rootPath = new File(".");
    try {
      homePath = rootPath.getCanonicalPath().replace("/bin/production/AndroidStarter", "");
    } catch (IOException e) {
      e.printStackTrace();
    }

    fileMap = new HashMap<>();
    fileMap.put(FileNames.ACTIVITY_MAIN_XML, FileUtils.makePathWithSlash(homePath, "AndroidModule/app/src/main/res/layout"));
    fileMap.put(FileNames.LAYOUT_LIST_ITEM_XML, FileUtils.makePathWithSlash(homePath, "AndroidModule/app/src/main/res/layout"));
    fileMap.put(FileNames.RECYCLERVIEWACTIVITY, FileUtils.makePathWithSlash(homePath, "AndroidModule/app/src/main/java/com/kimkevin/module"));
    fileMap.put(FileNames.RECYCLERVIEWADAPTER, FileUtils.makePathWithSlash(homePath, "AndroidModule/app/src/main/java/com/kimkevin/module/adapter"));
    fileMap.put(FileNames.BUILD_GRADLE, FileUtils.makePathWithSlash(homePath, "AndroidModule/app"));
  }

  public String getPath(String key) {
    return fileMap.get(key);
  }
}
