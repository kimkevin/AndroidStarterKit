package com.androidstarterkit;

import java.util.HashMap;
import java.util.Map;

public class AndroidModule {
  public static String APPLICATION_ID = "com.androidstarterkit.module";
  public static String APP_MODULE_PATH = "ask-module";

  private Map<String, String> fileMap;

  private String homePath;

  public AndroidModule() {
    homePath = FileUtils.getRootPath();

    fileMap = new HashMap<>();

    fileMap.put(FileNames.BUILD_GRADLE, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH));

    fileMap.put(FileNames.LAYOUT_LIST_ITEM_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.ACTIVITY_RECYCLERVIEW_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.RECYCLERVIEW_ACTIVITY, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module"));
    fileMap.put(FileNames.RECYCLERVIEW_ADAPTER, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "/src/main/java/com/androidstarterkit/module/adapter"));

    fileMap.put(FileNames.LISTVIEW_ACTIVITY, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module"));
    fileMap.put(FileNames.LISTVIEW_ADAPTER, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "/src/main/java/com/androidstarterkit/module/adapter"));
    fileMap.put(FileNames.ACTIVITY_LISTVIEW_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));

    fileMap.put(FileNames.COFFEE_TYPE, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module"));

    fileMap.put(FileNames.SLIDINGTABLAYOUT_ACTIVITY, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module"));
    fileMap.put(FileNames.SLIDINGICONTABLAYOUT_ACTIVITY, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module"));
    fileMap.put(FileNames.SLIDINGTAB_ADAPTER, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "/src/main/java/com/androidstarterkit/module/adapter"));
    fileMap.put(FileNames.SLIDINGICONTAB_ADAPTER, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "/src/main/java/com/androidstarterkit/module/adapter"));
    fileMap.put(FileNames.ACTIVITY_SLIDINGTABLAYOUT_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.FRAGMNET_MAIN_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.TAB_IMAGE_LAYOUT_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.TAB_TEXT_LAYOUT_XML, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/res/layout"));
    fileMap.put(FileNames.SLIDINGTAB_FRAGMNET, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module"));

    fileMap.put(FileNames.SLIDINGTABLAYOUT, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module/widgets"));
    fileMap.put(FileNames.SLIDINGTABSTRIP, FileUtils.linkPathWithSlash(homePath, APP_MODULE_PATH, "src/main/java/com/androidstarterkit/module/widgets"));
  }

  public String getPath(String key) {
    return fileMap.get(key);
  }
}
