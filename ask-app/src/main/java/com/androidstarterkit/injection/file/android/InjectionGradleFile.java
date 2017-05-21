package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.GradleConfig;
import com.androidstarterkit.injection.model.Snippet;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InjectionGradleFile extends InjectionBaseFile<GradleConfig> {
  private static final String DEPENDENCIES_ELEMENT_NAME = "dependencies";
  private static final String COMPILE_CONFIGURATION_FORMAT = "compile '" + SyntaxConstraints.REPLACE_STRING + "'";

  public InjectionGradleFile(String pathname) {
    super(pathname);
  }

  public void addDependency(String... externalLibraries) {
    for (String externalLibrary : externalLibraries) {
      GradleConfig config = new GradleConfig(getPath().substring(0, getPath().lastIndexOf(File.separator)), getName());
      Snippet dependencySnippet = new Snippet(Collections.singletonList(DEPENDENCIES_ELEMENT_NAME)
          , COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConstraints.REPLACE_STRING, externalLibrary));
      config.addSnippet(dependencySnippet);

      addConfig(config);
    }
  }

  @Override
  public void apply() {
    List<GradleElement> rootGradleElements = createGradleElements();

    for (GradleConfig config : configs) {
      for (Snippet snippet : config.getSnippets()) {
        List<String> elements = snippet.getElements();
        if (elements.size() > 0) {
          for (int i = 0, li = elements.size(); i < li; i++) {
            GradleElement foundGradleElement = findGradleElement(rootGradleElements, elements.subList(0, i + 1));

            if (foundGradleElement == null) {
              if (i == 0) {
                codelines.addAll(createNewElementBlock(elements.get(i)));
                rootGradleElements = createGradleElements();
              } else {
                GradleElement parentGradleElement = findGradleElement(rootGradleElements, elements.subList(0, i));
                if (parentGradleElement != null) {
                  codelines.addAll(parentGradleElement.openBracketIndex + 1
                      , SyntaxUtils.appendIndentForPrefix(createNewElementBlock(elements.get(i)), i));
                  rootGradleElements = createGradleElements();
                }
              }
            }

            foundGradleElement = findGradleElement(rootGradleElements, elements.subList(0, i + 1));
            if (foundGradleElement != null && i == elements.size() - 1) {
              codelines.addAll(foundGradleElement.openBracketIndex + 1
                  , SyntaxUtils.appendIndentForPrefix(snippet.getCodelines(), i + 1));
              rootGradleElements = createGradleElements();
            }
          }
        } else {
          codelines.addAll(snippet.getCodelines());
          rootGradleElements = createGradleElements();
        }
      }
    }

    super.apply();
  }

  private List<GradleElement> createGradleElements() {
    List<GradleElement> gradleElements = new ArrayList<>();
    Stack<GradleElement> allElementStack = new Stack<>();

    int openBracketElementCount = 0;
    int openBracketInvalidCount = 0;
    for (int i = 0, li = codelines.size(); i < li; i++) {
      String codeline = FileUtils.getStringWithRemovedComment(codelines.get(i));
      String elementName = getValidElement(codeline);
      if (elementName != null) {
        GradleElement gradleElement = new GradleElement(elementName, i);
        if (openBracketElementCount > 0) {
          allElementStack.peek().addChildElement(gradleElement);
        } else {
          gradleElements.add(gradleElement);
        }
        allElementStack.push(gradleElement);
      }

      int openCurlyBracketCount = FileUtils.getOpenCurlyBracketCount(codeline);
      int closeCurlyBracketCount = FileUtils.getCloseCurlyBracketCount(codeline);

      if (isInvalidOpenBracket(codeline)) {
        openBracketInvalidCount += openCurlyBracketCount;
      } else {
        openBracketElementCount += openCurlyBracketCount;
      }

      if (closeCurlyBracketCount > 0 && openBracketInvalidCount <= 0) {
        popElementStack(allElementStack, closeCurlyBracketCount, i);
      }

      if (closeCurlyBracketCount > 0) {
        if (openBracketInvalidCount >= closeCurlyBracketCount) {
          openBracketInvalidCount -= closeCurlyBracketCount;
        } else {
          closeCurlyBracketCount -= openBracketInvalidCount;
          openBracketInvalidCount = 0;
          openBracketElementCount -= closeCurlyBracketCount;
        }
      }
    }
    return gradleElements;
  }

  private GradleElement findGradleElement(List<GradleElement> childGradleElements, List<String> elementNames) {
    int size = elementNames.size();

    for (GradleElement gradleElement : childGradleElements) {
      if (gradleElement.getName().equals(elementNames.get(0))) {
        if (size <= 1) {
          return gradleElement;
        } else {
          return findGradleElement(gradleElement.childElements, elementNames.subList(1, size));
        }
      }
    }
    return null;
  }

  private List<String> createNewElementBlock(String element) {
    List<String> lines = new ArrayList<>();
    lines.add("");
    lines.add(element + " {");
    lines.add("}");
    return lines;
  }

  private String getValidElement(String codeline) {
    Pattern pat = Pattern.compile("\\s*(\\w+)\\s*\\{\\s*");
    Matcher matcher = pat.matcher(codeline);

    return matcher.find() ? matcher.group(1).trim() : null;
  }


  private boolean isInvalidOpenBracket(String codeline) {
    String[] invalidPattern = {
        "\\s*\\w+\\s*\\('[\\w.\\-_@:]+'\\s*\\)\\s*\\{", // INVALID : compile('com.crashlytics.sdk.android:answers:1.3.13@aar') {
        "\\s*\\w+\\s*\\('[\\w.\\-_@:]+'\\s*,\\s*\\{\\s*", // INVALID : androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        "\\s*task\\s+\\w+\\s*\\(\\s*\\w+\\s*:\\s*\\w+\\)\\s*\\{" // INVALID : task clean(type: Delete) {
    };

    for (String pattern : invalidPattern) {
      Pattern pat = Pattern.compile(pattern);
      Matcher matcher = pat.matcher(codeline);
      if (matcher.find()) {
        return true;
      }
    }
    return false;
  }

  private void popElementStack(Stack<GradleElement> allElementStack, int closeCurlyBracketCount, int lineIndex) {
    for (int j = 0; j < closeCurlyBracketCount; j++) {
      GradleElement topElement = allElementStack.pop();
      topElement.setCloseBracketIndex(lineIndex);
    }
  }

  private class GradleElement {
    private String name;
    private int openBracketIndex;
    private int closeBracketIndex;
    private List<GradleElement> childElements;

    GradleElement(String name, int openBracketIndex) {
      this.name = name;
      this.openBracketIndex = openBracketIndex;
      this.closeBracketIndex = -1;
      this.childElements = new ArrayList<>();
    }

    public String getName() {
      return name;
    }

    void setCloseBracketIndex(int closeBracketIndex) {
      this.closeBracketIndex = closeBracketIndex;
    }

    void addChildElement(GradleElement element) {
      childElements.add(element);
    }

    @Override
    public String toString() {
      return "GradleElement: {" + "name='" + name + '\''
          + ", openBracketIndex=" + openBracketIndex
          + ", closeBracketIndex=" + closeBracketIndex
          + ", childElements=" + childElements + "}";
    }
  }
}
