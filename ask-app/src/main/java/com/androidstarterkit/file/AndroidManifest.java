package com.androidstarterkit.file;

import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.model.Permission;
import com.androidstarterkit.util.FileUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidManifest extends BaseFile {
  private static final String USES_PERMISSION = "<uses-permission android:name=\"android.permission."
      + SyntaxConfig.REPLACE_STRING + "\"/>";

  public AndroidManifest(String pathname) {
    super(pathname, "AndroidManifest.xml");
  }

  public void addPermissions(Permission... permissions) {
    if (permissions == null || permissions.length <= 0) {
      return;
    }

    List<String> lineList = FileUtils.readFile(this);

    for (Permission permission : permissions) {
      lineList = addLineToElement("manifest",
          permission,
          lineList);
    }

    FileUtils.writeFile(this, lineList);
  }

  private List<String> addLineToElement(String elementName, Permission permission, List<String> lineList) {
    String indent = SyntaxConfig.DEFAULT_INDENT;

    String reg = "\\<\\/\\s*" + elementName + "\\s*\\>";
    Pattern pattern = Pattern.compile(reg);

    for (int i = 0, li = lineList.size(); i < li; i++) {
      final String codeLine = lineList.get(i);

      if (codeLine.contains(permission.name())) {
        return lineList;
      }

      Matcher matcher = pattern.matcher(codeLine);

      if (matcher.find()) {
        lineList.add(i, indent + getPermissionToString(permission));
      }
    }

    return lineList;
  }

  private String getPermissionToString(Permission permission) {
    return USES_PERMISSION.replace(SyntaxConfig.REPLACE_STRING, permission.name());
  }
}
