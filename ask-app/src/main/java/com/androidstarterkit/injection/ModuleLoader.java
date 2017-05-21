package com.androidstarterkit.injection;


import com.androidstarterkit.injection.model.Config;
import com.androidstarterkit.injection.model.GradleConfig;
import com.androidstarterkit.injection.model.JavaConfig;
import com.androidstarterkit.injection.model.ManifestConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModuleLoader {

  private List<CodeGenerator> codeGenerators;

  public ModuleLoader() {
    codeGenerators = new ArrayList<>();
  }

  public void generateCode() {
    codeGenerators.forEach(CodeGenerator::apply);
  }

  public void addCodeGenerator(CodeGenerator codeGenerator) {
    if (!codeGenerators.contains(codeGenerator)) {
      codeGenerators.add(codeGenerator);
    }
  }

  public void addManifestConfigs(List<ManifestConfig> configs) {
    if (configs != null) {
      configs.forEach(this::addConfig);
    }
  }

  public void addJavaConfigs(List<JavaConfig> configs) {
    if (configs != null) {
      configs.forEach(this::addConfig);
    }
  }

  public void addGradleConfigs(List<GradleConfig> configs) {
    if (configs != null) {
      configs.forEach(this::addConfig);
    }
  }

  private void addConfig(Config config) {
    if (config != null) {
      CodeGenerator codeGenerator = findCodeGenerator(config);
      if (codeGenerator != null) {
        codeGenerator.addConfig(config);
      }
    }
  }

  private CodeGenerator findCodeGenerator(Config config) {
    for (CodeGenerator codeGenerator : codeGenerators) {
      File file = (File) codeGenerator;
      if (config.getFullPathname().equalsIgnoreCase(file.getPath())) {
        return codeGenerator;
      }
    }
    return null;
  }
}
