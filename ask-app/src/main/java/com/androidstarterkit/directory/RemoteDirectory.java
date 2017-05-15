package com.androidstarterkit.directory;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.command.TabType;
import com.androidstarterkit.constraints.SyntaxConstraints;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.MainActivity;
import com.androidstarterkit.file.SlidingTabFragment;
import com.androidstarterkit.injection.file.android.InjectionJavaFile;
import com.androidstarterkit.injection.model.LayoutGroup;
import com.androidstarterkit.tool.ClassInfo;
import com.androidstarterkit.util.FileUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoteDirectory extends Directory {
  public static final String MODULE_NAME = "ask-remote-module";
  public static final String SAMPLE_ACTIVITY_NAME = "SampleActivity.java";
  public static final String PACKAGE_NAME = "com/androidstarterkit/module";

  private MainActivity mainActivity;
  private SlidingTabFragment sampleTabFragment;

  private MainActivity bakMainActivity;
  private SlidingTabFragment bakSampleTabFragment;

  public RemoteDirectory(String projectPathname) throws CommandException {
    super(projectPathname + "/" + MODULE_NAME
        , new String[] { "java", "xml", "gradle", "json" }
        , new String[] { "build", "libs" });

    mainActivity = new MainActivity(getChildDirPath(SAMPLE_ACTIVITY_NAME) + "/" + SAMPLE_ACTIVITY_NAME);
    sampleTabFragment = new SlidingTabFragment(getChildDirPath(SlidingTabFragment.FILE_NAME) + "/" + SlidingTabFragment.FILE_NAME);
  }

  public String getFilePathFromJavaDir(String key) {
    String applicationIdPath = FileUtils.changeDotToSlash(applicationId);

    int index;
    try {
      index = getChildDirPath(key).indexOf(applicationIdPath);
    } catch (NullPointerException exception) {
      return null;
    }

    try {
      return getChildDirPath(key).substring(index).replace(applicationIdPath, "");
    } catch (StringIndexOutOfBoundsException exception) {
      return getChildDirPath(key);
    }
  }

  public MainActivity getMainActivity() {
    return mainActivity;
  }

  public void changeMainFragmentByTabType(TabType tabType, List<LayoutGroup> layoutGroups) {
    bakMainActivity = new MainActivity(mainActivity.getPath());

    String fragmentName;
    if (tabType != null) {
      fragmentName = tabType.getFragmentName();
    } else {
      fragmentName = layoutGroups.get(0).getClassName();
    }

    Pattern pattern = Pattern.compile("\\.add\\(R\\.id\\.\\w+,\\s*new\\s*(\\w+)\\s*\\(");

    List<String> codelines = mainActivity.getCodelines();
    for (int i = 0, li = codelines.size(); i < li; i++) {
      String codeline = codelines.get(i);
      Matcher matcher = pattern.matcher(codeline);
      if (matcher.find()) {
        codelines.set(i, codeline.replace(matcher.group(1), fragmentName));
      }
    }

    mainActivity.apply();

    List<ClassInfo> classInfos = new ArrayList<>();
    classInfos.add(new ClassInfo(fragmentName));
    defineImportClassString(mainActivity, classInfos);
  }

  public void injectLayoutModulesToFragment(List<LayoutGroup> layoutGroups) {
    if (layoutGroups.size() <= 1) {
      return;
    }

    bakSampleTabFragment = new SlidingTabFragment(sampleTabFragment.getPath());

    List<String> codelines = sampleTabFragment.getCodelines();

    Iterator<String> iterator = codelines.iterator();
    while (iterator.hasNext()) {
      String codeline = iterator.next();
      if (codeline.contains("fragmentInfos.add")) {
        iterator.remove();
      }
    }

    for (int i = 0, li = codelines.size(); i < li; i++) {
      String codeline = codelines.get(i);

      if (codeline.contains("List<FragmentInfo> fragmentInfos = new ArrayList<>();") && layoutGroups.size() > 0) {
        final String ADD_FRAGMENT_TO_LIST_STRING = "fragmentInfos.add(new FragmentInfo(" + SyntaxConstraints.REPLACE_STRING + ".class));";

        String layoutCodeline = "";

        final String intent = FileUtils.getIndentOfLine(codeline);

        for (LayoutGroup layoutGroup : layoutGroups) {
          layoutCodeline += intent + ADD_FRAGMENT_TO_LIST_STRING.replace(SyntaxConstraints.REPLACE_STRING, layoutGroup.getClassName()) + "\n";
        }

        sampleTabFragment.setCodeline(i + 1, layoutCodeline);
      }
    }

    sampleTabFragment.apply();
  }

  private void defineImportClassString(InjectionJavaFile javaFile, List<ClassInfo> addedClassInfos) {
    List<String> importedClassStrings = new ArrayList<>();
    for (int i = 0, li = addedClassInfos.size(); i < li; i++) {
      String classname = addedClassInfos.get(i).getName();
      if (getFilePathFromJavaDir(classname + Extension.JAVA.toString()) != null) {
        String importedClassString = SyntaxConstraints.IDENTIFIER_IMPORT + " "
            + applicationId
            + getFilePathFromJavaDir(classname + Extension.JAVA.toString()).replaceAll("/", ".") + "."
            + classname + ";";
        if (!importedClassStrings.contains(importedClassString)) {
          importedClassStrings.add(importedClassString);
        }
      }
    }

    List<String> codelines = javaFile.getCodelines();
    for (int i = 0, li = codelines.size(); i < li; i++) {
      String codeline = codelines.get(i);

      if (codeline.contains(SyntaxConstraints.IDENTIFIER_PACKAGE)) {
        javaFile.addCodelines(i + 1, importedClassStrings);
        break;
      }
    }

    javaFile.apply();
  }

  public void recover() {
    if (bakMainActivity != null) {
      bakMainActivity.apply();
    }

    if (bakSampleTabFragment != null) {
      bakSampleTabFragment.apply();
    }
  }
}
