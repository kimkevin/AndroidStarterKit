package com.androidstarterkit.file;

import com.androidstarterkit.exception.MultipleFlavorsException;
import com.androidstarterkit.injection.file.android.InjectionGradleFile;
import com.androidstarterkit.util.FileUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildGradle extends InjectionGradleFile {
  private static final String TAG = BuildGradle.class.getSimpleName();

  public static final String FILE_NAME = "build.gradle";

  private String applicationId;
  private String supportLibraryVersion;

  public BuildGradle(String modulePath) {
    super(modulePath + "/" + FILE_NAME);

    for (String line : codelines) {
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

    checkMultipleFlavors();
  }

  public String getApplicationId() {
    return applicationId;
  }

  public String getSupportLibraryVersion() {
    return supportLibraryVersion;
  }

  private void checkMultipleFlavors() {
    List<GradleElement> gradleElements = createGradleElements();

    GradleElement androidGradleElement = getGradleElement(gradleElements, "android");
    if (androidGradleElement != null) {
      GradleElement productFlavorsGradleElement = getGradleElement(
          androidGradleElement.getChildElements(), "productFlavors");

      if (productFlavorsGradleElement != null
          && productFlavorsGradleElement.getChildElements().size() > 0) {
        throw new MultipleFlavorsException();
      }
    }
  }

  private GradleElement getGradleElement(List<GradleElement> childElements, String name) {
    for (GradleElement childGradleElement : childElements) {
      if (childGradleElement.getName().equals(name)) {
        return childGradleElement;
      }
    }

    return null;
  }
}
