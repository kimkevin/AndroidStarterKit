package com.androidstarterkit.file;

import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.util.FileUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildGradle extends BaseFile {
  private static final String TAG = BuildGradle.class.getSimpleName();

  public static final String FILE_NAME = "build.gradle";
  private static final String ELEMENT_DEPENDENCIES_NAME = "dependencies";
  private static final String COMPILE_CONFIGURATION_FORMAT = "compile '" + SyntaxConstraints.REPLACE_STRING + "'";

  private List<String> lineList;

  private String applicationId;
  private String supportLibraryVersion;

  public BuildGradle(String modulePath) {
    super(modulePath, FILE_NAME);

    lineList = FileUtils.readFile(this);

    for (String line : lineList) {
      if (line.contains("applicationId")) {
        applicationId = FileUtils.getStringBetweenQuotes(line);
      } else if (line.contains("com.android.support:appcompat-v7")) {
        String reg = ":([0-9]*\\.[0-9]*\\.[0-9]*)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
          supportLibraryVersion = matcher.group(1);
        }
      }
    }
  }

  public String getApplicationId() {
    return applicationId;
  }

  public String getSupportLibraryVersion() {
    return supportLibraryVersion;
  }

  /**
   * Add dependencies on compile configuration
   *
   * @param externalLibraries is strings
   */
  public void addDependency(String... externalLibraries) {
    if (externalLibraries.length <= 0) {
      return;
    }

    for (String externalLibrary : externalLibraries) {
      lineList = addLineToElement(
          ELEMENT_DEPENDENCIES_NAME,
          COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConstraints.REPLACE_STRING, externalLibrary),
          lineList);
    }

    FileUtils.writeFile(this, lineList);
  }

  /**
   * Add line to specific object by a string list of scope
   *
   * @param elementName is keyword to find scope
   * @param dependencyString       is a string that is needed to add
   * @param lineList is string list of scope
   * @return string list was added to line such as external library
   */
  private List<String> addLineToElement(String elementName, String dependencyString, List<String> lineList) {
    boolean isFoundScope = false;
    String indent = "";
    int scopeCnt = 0;

    for (int i = 0, li = lineList.size(); i < li; i++) {
      final String codeLine = lineList.get(i);

      if (codeLine.contains(elementName)) {
        isFoundScope = true;

        if (i + 1 < li) {
          indent = FileUtils.getIndentOfLine(lineList.get(i + 1));
        } else {
          indent = SyntaxConstraints.DEFAULT_INDENT;
        }
      }

      if (isFoundScope) {
        if (codeLine.contains("{")) {
          scopeCnt++;
        } else if (codeLine.contains("}")) {
          scopeCnt--;
        }
      }

      if (isFoundScope && scopeCnt == 0) {
        if (lineList.contains(indent + dependencyString)) {
          continue;
        }

        lineList.add(i, indent + dependencyString);
        return lineList;
      }
    }

    return lineList;
  }

  public void print() {
    for (String line : lineList) {
      System.out.println(line);
    }
  }
}
