package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.SyntaxConstraints;
import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.CodeBlock;
import com.androidstarterkit.util.SyntaxUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InjectionGradleFile extends InjectionBaseFile {
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
    if (externalLibraries.length <= 0) {
      return;
    }

    for (String externalLibrary : externalLibraries) {
      CodeBlock newCodeBlock = new CodeBlock(Collections.singletonList(ELEMENT_DEPENDENCIES_NAME)
          , COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConstraints.REPLACE_STRING, externalLibrary));

      if (configCodeBlocks == null) {
        addCodeBlock(newCodeBlock);
      } else {
        boolean isExisted = false;

        for (CodeBlock codeBlock : configCodeBlocks) {
          if (codeBlock.getCodelines().get(0).equals(newCodeBlock.getCodelines().get(0))) {
            isExisted = true;
          }
        }

        if (!isExisted) {
          addCodeBlock(newCodeBlock);
        }
      }
    }
  }

  @Override
  public void apply() {
    for (CodeBlock codeblock : configCodeBlocks) {
      Queue<String> elementQueue = new LinkedList<>();

      if (codeblock.getElements() != null && codeblock.getElements().size() > 0) {
        elementQueue.addAll(codeblock.getElements());

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
            codelines.addAll(i + 1, SyntaxUtils.addIndentToCodeline(
                deduplicatedCodelines(codeblock.getCodelines()), codeblock.getElements().size()));
            break;
          }
        }
      } else {
        codelines.addAll(deduplicatedCodelines(codeblock.getCodelines()));
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
