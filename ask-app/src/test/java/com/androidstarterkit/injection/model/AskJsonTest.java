package com.androidstarterkit.injection.model;


import com.androidstarterkit.util.FileUtils;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AskJsonTest {
  private AskJson askJson;

  @Before
  public void setUp() throws Exception {
    Gson gson = new Gson();
    List<String> codelines = Files.readAllLines(Paths.get(FileUtils.getRootPath() + "/ask-app/ask.fabric.json")
        , Charset.defaultCharset());
    askJson = gson.fromJson(FileUtils.toString(codelines), AskJson.class);
  }

  @Test
  public void fabricJsonTest() throws Exception {
    assertNull(askJson.getLayoutGroups());
    assertNotNull(askJson.getModuleGroups());
    assertEquals(1, askJson.getModuleGroups().size());

    ModuleGroup moduleGroup = askJson.getModuleGroups().get(0);
    assertEquals(1, moduleGroup.getGroupGradleConfigs().size());
    assertEquals(1, moduleGroup.getGroupManifestConfigs().size());
    assertEquals(2, moduleGroup.getModules().size());

    ManifestConfig manifestConfig = moduleGroup.getGroupManifestConfigs().get(0);
    assertNotNull(manifestConfig);
    assertEquals(2, manifestConfig.getManifestElements().size());

    Module module = moduleGroup.getModules().get(0);
    assertNotNull(module);
    assertNull(module.getManifestConfigs());
    assertEquals(1, module.getJavaConfigs().size());
    assertEquals(1, module.getGradleConfigs().size());
  }
}
