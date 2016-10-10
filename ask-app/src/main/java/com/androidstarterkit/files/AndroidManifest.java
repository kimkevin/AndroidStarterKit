package com.androidstarterkit.files;

import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.utils.FileUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidManifest extends BaseFile {
  public static final String MANIFEST_ELEMENT_NAME = "manifest";
  public static final String MAIN_RELATIVE_PATH_FROM_MODULE = "src/main";

  public AndroidManifest(String pathname) {
    super(pathname, "AndroidManifest.xml");
  }

  public void addPermissions(String... permissions) {
    if (permissions == null || permissions.length <= 0) {
      return;
    }

    List<String> lineList = FileUtil.readFile(this);

    for (String permission : permissions) {
      lineList = addLineToElement(MANIFEST_ELEMENT_NAME,
          permission,
          lineList);
    }

    FileUtil.writeFile(this, lineList);
  }

  private List<String> addLineToElement(String elementName, String permission, List<String> lineList) {
    String indent = SyntaxConfig.DEFAULT_INDENT;

    String reg = "\\<\\/\\s*" + elementName + "\\s*\\>";

    Pattern pat = Pattern.compile(reg);
    for (int i = 0, li = lineList.size(); i < li; i++) {
      final String codeLine = lineList.get(i);

      Matcher matcher = pat.matcher(codeLine);

      if (matcher.find()) {
        lineList.add(i, indent + permission);
      }
    }

    return lineList;
  }
}
