package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.GradleConfig;
import com.androidstarterkit.injection.model.Snippet;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InjectionGradleFile extends InjectionBaseFile<GradleConfig> {
  private static final String ELEMENT_DEPENDENCIES_NAME = "dependencies";
  private static final String COMPILE_CONFIGURATION_FORMAT = "compile '" + SyntaxConstraints.REPLACE_STRING + "'";

  public InjectionGradleFile(String pathname) {
    super(pathname);
  }

  /**
   * Add dependencies on compile configuration
   *
   * @param externalLibraries is strings
   */
  public void addDependency(String... externalLibraries) {
    for (String externalLibrary : externalLibraries) {
      GradleConfig config = new GradleConfig(getPath().substring(0, getPath().lastIndexOf(File.separator)), getName());
      Snippet dependencySnippet = new Snippet(Collections.singletonList(ELEMENT_DEPENDENCIES_NAME)
          , COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConstraints.REPLACE_STRING, externalLibrary));
      config.addSnippet(dependencySnippet);

      addConfig(config);
    }
  }

  @Override
  public void apply() {
    for (GradleConfig gradleConfig : configs) {
      for (Snippet snippet : gradleConfig.getSnippets()) {
        Queue<String> elementQueue = new LinkedList<>();

        if (snippet.getElements() != null && snippet.getElements().size() > 0) {
          elementQueue.addAll(snippet.getElements());

          boolean isFound = false;
          for (int i = 0, li = codelines.size(); i < li; i++) {
            String codeline = codelines.get(i);

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
              codelines.addAll(i + 1, SyntaxUtils.addIndentToCodeline(deduplicatedCodelines(snippet.getCodelines())
                  , snippet.getElements().size()));
              break;
            }
          }
        } else {
          codelines.addAll(deduplicatedCodelines(snippet.getCodelines()));
        }
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
