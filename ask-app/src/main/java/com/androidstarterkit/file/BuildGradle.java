package com.androidstarterkit.file;

import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.model.CodeBlock;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildGradle extends BaseFile {
  private static final String TAG = BuildGradle.class.getSimpleName();

  public static final String FILE_NAME = "build.gradle";
  private static final String ELEMENT_DEPENDENCIES_NAME = "dependencies";
  private static final String COMPILE_CONFIGURATION_FORMAT = "compile '" + SyntaxConstraints.REPLACE_STRING + "'";

  private String applicationId;
  private String supportLibraryVersion;

  public BuildGradle(String modulePath) {
    super(modulePath, FILE_NAME);

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
      addCodeBlock(new CodeBlock(Collections.singletonList(ELEMENT_DEPENDENCIES_NAME)
          , COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConstraints.REPLACE_STRING, externalLibrary)));
    }
  }

  @Override
  public void apply() {
    for (CodeBlock codeblock : configCodeBlocks) {
      Queue<String> elementQueue = new LinkedList<>();

      if (codeblock.getElements() != null && codeblock.getElements().size() > 0) {
        elementQueue.addAll(codeblock.getElements());

        boolean isFound = false;
        for (int i = 0, li = lineList.size(); i < li; i++) {
          String codeline = lineList.get(i);

          final String element = matchedElement(codeline);
          if (element != null) {
            String elementPeek = elementQueue.peek();

            if (element.equals(elementPeek)) {
              elementQueue.remove();
            }

            if (element.equals(elementPeek) && elementQueue.isEmpty()) {
              isFound = true;
            }
          }

          if (isFound) {
            lineList.addAll(i + 1, SyntaxUtils.addIndentToCodeline(codeblock.getCodelines(), codeblock.getElements().size()));
            break;
          }
        }
      } else {
        lineList.addAll(codeblock.getCodelines());
      }
    }

    super.apply();
  }

  private String matchedElement(String codeline) {
    Pattern pat = Pattern.compile("\\s*(\\w+)\\s*\\{\\s*");
    Matcher matcher = pat.matcher(codeline);

    return matcher.find() ? matcher.group(1).trim() : null;
  }
}
