package com.androidstarterkit.files;

import com.androidstarterkit.config.SyntaxConfig;
import com.androidstarterkit.utils.FileUtil;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class BuildGradleFile extends BaseFile {
  public static final String DEPENDENCIES_ELEMENT_NAME = "dependencies";
  public static final String COMPILE_CONFIGURATION_FORMAT = "compile '" + SyntaxConfig.REPLACE_STRING + "'";

  private List<String> lineList;

  public BuildGradleFile(String modulePath) {
    super(modulePath, "build.gradle");

    lineList = FileUtil.readFile(this);
  }

  public String getApplicationId() {
    String applicationId = null;
    try {
      Scanner scanner = new Scanner(this);

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        if (line.contains("applicationId")) {
          applicationId = FileUtil.getStringBetweenQuotes(line);
          break;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return applicationId;
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
          DEPENDENCIES_ELEMENT_NAME,
          COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConfig.REPLACE_STRING, externalLibrary),
          lineList);
    }

    FileUtil.writeFile(this, lineList);
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

    for (int i = 0, li = lineList.size(); i < li; i++) {
      final String codeLine = lineList.get(i);

      if (codeLine.contains(elementName)) {
        isFoundScope = true;

        if (i + 1 < li) {
          indent = FileUtil.getIndentOfLine(lineList.get(i + 1));
        } else {
          indent = SyntaxConfig.DEFAULT_INDENT;
        }
      }

      if (isFoundScope && codeLine.contains("}")) {
        if (lineList.contains(indent + dependencyString)) {
          continue;
        }

        lineList.add(i, indent + dependencyString);
        return lineList;
      }
    }

    return lineList;
  }
}
