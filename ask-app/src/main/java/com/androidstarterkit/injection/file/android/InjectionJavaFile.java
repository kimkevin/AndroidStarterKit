package com.androidstarterkit.injection.file.android;


import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.injection.file.base.InjectionBaseFile;
import com.androidstarterkit.injection.model.JavaConfig;
import com.androidstarterkit.injection.model.Method;
import com.androidstarterkit.tool.JavaLineScanner;
import com.androidstarterkit.tool.MatcherTask;
import com.androidstarterkit.util.FileUtils;
import com.androidstarterkit.util.SyntaxUtils;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InjectionJavaFile extends InjectionBaseFile<JavaConfig> {

  public InjectionJavaFile(String fullPathname) {
    super(fullPathname);
  }

  public InjectionJavaFile(String pathname, String filenameEx) {
    super(pathname, filenameEx);
  }

  @Override
  public void apply() {
    for (JavaConfig config : configs) {
      ListIterator<String> iterator = codelines.listIterator();
      while (iterator.hasNext()) {
        String codeline = iterator.next();

        if (isFoundPackage(codeline) && config.getImports() != null) {
          config.getImports().forEach(iterator::add);
        }

        String classIndent = "";
        if (config.getFileName().equalsIgnoreCase(matchedClass(codeline)) && config.getFields() != null) {
          classIndent = FileUtils.getIndentOfLine(codeline);
          for (String field : config.getFields()) {
            iterator.add(classIndent + SyntaxConstraints.DEFAULT_INDENT + field);
          }
        }

        for (Method method : config.getMethods()) {
          if (method.getName().equalsIgnoreCase(matchedMethod(codeline))) {
            MatcherTask matcherTask = new MatcherTask("\\s+super\\." + method.getName() + "(\\w*)", codelines.get(iterator.nextIndex()));
            matcherTask.match(0, v -> iterator.next());

            for (String line : method.getLines()) {
              iterator.add(SyntaxUtils.appendIndentForPrefix(classIndent + line, 2));
            }

            method.setImported(true);
          }
        }
      }

      injectConfigForNotFoundMethod(config);
    }

    super.apply();
  }

  private void injectConfigForNotFoundMethod(JavaConfig config) {
    List<Method> unimportedMethods = unimportedMethods(config.getMethods());
    if (unimportedMethods.size() > 0) {
      for (Method method : unimportedMethods) {
        JavaLineScanner javaLineScanner = new JavaLineScanner();

        List<String> methodStrings = method.createMethodAsString(1);
        codelines.addAll(javaLineScanner.getCloseBracketIndex(codelines), methodStrings);

        method.setImported(true);
      }
    }
  }

  private List<Method> unimportedMethods(List<Method> methods) {
    return methods.stream()
        .filter(method -> !method.isImported())
        .collect(Collectors.toList());
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
    Pattern pat = Pattern.compile("\\w+\\s+(\\w+)\\s*\\(.*\\)");
    Matcher matcher = pat.matcher(codeline);

    return matcher.find() ? matcher.group(1) : null;
  }
}
