package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.JavaConfig;
import com.androidstarterkit.injection.model.Method;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InjectionJavaFile extends InjectionBaseFile<JavaConfig> {

  public InjectionJavaFile(String pathname) {
    super(pathname);
  }

  @Override
  public void apply() {
    for (JavaConfig config : configs) {
      ListIterator<String> iterator = codelines.listIterator();
      while (iterator.hasNext()) {
        String codeline = iterator.next();

        if (isFoundPackage(codeline)) {
          config.getImports().forEach(iterator::add);
        }

        String classIndent = "";
        if (config.getFileName().equalsIgnoreCase(matchedClass(codeline))) {
          classIndent = FileUtils.getIndentOfLine(codeline);
          for (String field : config.getFields()) {
            iterator.add(classIndent + SyntaxConstraints.DEFAULT_INDENT + field);
          }
        }

        for (Method method : config.getMethods()) {
          if (method.getName().equalsIgnoreCase(matchedMethod(codeline))) {
            Pattern pat = Pattern.compile("\\s+super\\." + method.getName() + "(\\w*)");
            Matcher matcher = pat.matcher(codelines.get(iterator.nextIndex()));
            if (matcher.find()) {
              iterator.next();
            }

            for (String line : method.getLines()) {
              iterator.add(SyntaxUtils.addIndentToCodeline(classIndent + line, 2));
            }
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
