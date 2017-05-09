package com.androidstarterkit.injection;

import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.injection.model.AskJson;
import com.androidstarterkit.util.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AskJsonBuilder {
  private PropertyLineConverter propertyLineConverter;
  private String dirPath;

  public AskJsonBuilder(String dirPath) {
    this.dirPath = dirPath;
    propertyLineConverter = new PropertyLineConverter();
  }

  public AskJsonBuilder addProperty(String propertyPatternStr, String convertedStr) {
    propertyLineConverter.addProperty(propertyPatternStr, convertedStr);
    return this;
  }

  public AskJson build() {
    AskJson askJson = null;

    List<File> jsonFiles = getJsonFiles(dirPath);

    Gson gson = new Gson();

    for (File jsonFile : jsonFiles) {
      try {
        List<String> codelines = Files.readAllLines(Paths.get(jsonFile.getPath()), Charset.defaultCharset());

        AskJson newAskJson = gson.fromJson(FileUtils.convertListStringToString(propertyLineConverter.replace(codelines))
            , AskJson.class);

        if (askJson == null) {
          askJson = newAskJson;
        } else {
          askJson.append(newAskJson);
        }
      } catch (IOException e) {
        throw new CommandException(CommandException.NOT_FOUND_ASK_JSON);
      }
    }

    return askJson;
  }

  private List<File> getJsonFiles(String dirPath) {
    List<File> jsonFiles = new ArrayList<>();

    File directory = new File(dirPath);

    File[] files = directory.listFiles(file
        -> file.getName().toLowerCase().endsWith(Extension.JSON.toString()));

    if (files == null) {
      throw new CommandException(CommandException.NOT_FOUND_ASK_JSON);
    }

    Collections.addAll(jsonFiles, files);

    return jsonFiles;
  }

  private class PropertyLineConverter {
    private Map<String, String> proerties;

    private PropertyLineConverter() {
      proerties = new HashMap<>();
    }

    private void addProperty(String propertyStr, String convertedStr) {
      proerties.put(propertyStr, convertedStr);
    }

    private String replace(String codeline) {
      Set<String> keySets = proerties.keySet();
      for (String propertyStr : keySets) {
        codeline = codeline.replaceAll(propertyStr, proerties.get(propertyStr));
      }
      return codeline;
    }

    private List<String> replace(List<String> codelines) throws IOException {
      for (int i = 0, li = codelines.size(); i < li; i++) {
        codelines.set(i, replace(codelines.get(i)));
      }

      return codelines;
    }
  }
}
