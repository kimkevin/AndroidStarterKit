package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.CodeBlock;
import com.androidstarterkit.util.SyntaxUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InjectionJavaFile extends InjectionBaseFile {

  public InjectionJavaFile(File file) {
    super(file.getPath());
  }

  @Override
  public void apply() {
    for (CodeBlock codeblock : configCodeBlocks) {
      Queue<String> elementQueue = new LinkedList<>();

      if (codeblock.getElements() != null && codeblock.getElements().size() > 0) {
        elementQueue.addAll(codeblock.getElements());

        boolean isClassFound = false;
        boolean isFinalFound = false;
        for (int i = 0, li = codelines.size(); i < li; i++) {
          String codeline = codelines.get(i);

          if (!isClassFound) {
            final String element = matchedClass(codeline);
            if (element != null) {
              String elementPeek = elementQueue.peek();

              if (element.equals(elementPeek)) {
                elementQueue.remove();
                isClassFound = true;
              }
            }
          }

          if (isClassFound) {
            int nextIndex = i + 1;

            if (!elementQueue.isEmpty()) {
              final String element = matchedMethod(codeline);

              if (element != null) {
                String elementPeek = elementQueue.peek();

                if (element.equals(elementPeek)) {
                  elementQueue.remove();

                  if (elementQueue.isEmpty()) {
                    isFinalFound = true;
                  }
                }
              }

              if (isFinalFound) {
                int insertedIndex;
                if (nextIndex < li) {
                  Pattern pat = Pattern.compile("\\s+super\\." + element + "(\\w*)");
                  Matcher matcher = pat.matcher(codelines.get(nextIndex));

                  insertedIndex = matcher.find() ? nextIndex + 1 : nextIndex;
                } else {
                  insertedIndex = nextIndex;
                }
                codelines.addAll(insertedIndex, SyntaxUtils.addIndentToCodeline(codeblock.getCodelines(), codeblock.getElements().size()));
                break;
              }
            } else {
              // below class definition
              codelines.addAll(nextIndex, SyntaxUtils.addIndentToCodeline(codeblock.getCodelines(), codeblock.getElements().size()));
              break;
            }
          }
        }
      } else {
        for (int i = 0, li = codelines.size(); i < li; i++) {
          String codeline = codelines.get(i);

          if (isFoundPackage(codeline)) {
            codelines.addAll(1, codeblock.getCodelines());
          }
        }
      }
    }

    super.apply();
  }

  private boolean isFoundPackage(String codeline) {
    Pattern pat = Pattern.compile("\\s*package\\s+\\w+");
    Matcher matcher = pat.matcher(codeline);

    return matcher.find();
  }

  private String matchedClass(String codeline) {
    Pattern pat = Pattern.compile("\\s+class\\s+(\\w+)\\s*");
    Matcher matcher = pat.matcher(codeline);

    return matcher.find() ? matcher.group(1) : null;
  }

  private String matchedMethod(String codeline) {
    Pattern pat = Pattern.compile("\\w+\\s+(\\w+)\\s*\\(.+\\)");
    Matcher matcher = pat.matcher(codeline);

    return matcher.find() ? matcher.group(1) : null;
  }
}
