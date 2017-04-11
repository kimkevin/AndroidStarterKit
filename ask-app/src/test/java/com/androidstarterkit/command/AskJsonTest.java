package com.androidstarterkit.command;


import com.androidstarterkit.android.api.Extension;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.file.BuildGradle;
import com.androidstarterkit.model.Config;
import com.androidstarterkit.model.LayoutGroup;
import com.androidstarterkit.model.Module;
import com.androidstarterkit.model.ModuleGroup;
import com.androidstarterkit.util.FileUtils;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AskJsonTest {
  private AskJson askJson;

  @Before
  public void setUp() throws Exception {
    Gson gson = new Gson();
    try {
      askJson = gson.fromJson(FileUtils.readFile(FileUtils.linkPathWithSlash(FileUtils.getRootPath(), "ask-app", AskJson.FILE_NAME))
          , AskJson.class);
    } catch (IOException e) {
      throw new CommandException(CommandException.NOT_FOUND_ASK_JSON);
    }
  }

  @Test
  public void getLayoutGroupByCommandTest() throws Exception {
    LayoutGroup layoutGroup = askJson.getLayoutGroupByCommand("gv");

    assertEquals(2, layoutGroup.getCommands().size());
    assertEquals(true, layoutGroup.getCommands().contains("gv"));
    assertNotNull(layoutGroup.getClassName());

    layoutGroup = askJson.getLayoutGroupByCommand("kv");
    assertEquals(null, layoutGroup);
  }

  @Test
  public void getModuleGroupByCommandTest() throws Exception {
    ModuleGroup moduleGroup = askJson.getModuleGroupByCommand("gv");
    assertEquals(null, moduleGroup);

    moduleGroup = askJson.getModuleGroupByCommand("fa");
    assertNotNull(moduleGroup);
    assertNotNull(moduleGroup.getName());
    assertNotNull(moduleGroup.getConfigFilenames());
    assertEquals(1, moduleGroup.getConfigFilenames().size());
    assertEquals(2, moduleGroup.getModules().size());
    assertEquals(2, moduleGroup.getGroupConfigs().size());

    for (Config config : moduleGroup.getGroupConfigs()) {
      assertNotNull(config.getPath());
      assertNotNull(config.getFileNameEx());
      assertNotNull(config.getCodeBlocks());
    }
  }

  @Test
  public void commandFirebaseAnalyticsTest() throws Exception {
    Module faModule = askJson.getModuleByCommand("fa");
    assertNotNull(faModule);
    assertNotNull(faModule.getClassNames());
    assertEquals(1, faModule.getClassNames().size());
    assertEquals("FireBaseAnalytics", faModule.getClassNames().get(0));

    assertNotNull(faModule.getCommands());
    assertEquals(2, faModule.getCommands().size());
    assertEquals(true, faModule.getCommands().contains("firebaseanalytics"));
    assertEquals(true, faModule.getCommands().contains("fa"));
    assertEquals(false, faModule.getCommands().contains("firebaseanalytic"));

    assertNotNull(faModule.getConfigs());
    assertEquals(2, faModule.getConfigs().size());

    Config mainJavaConfig = faModule.getConfigs().get(0);
    assertEquals(AskJson.JAVA_PATH_REPLACEMENT, mainJavaConfig.getPath());
    assertEquals(AskJson.MAIN_ACTIVITY_REPLACEMENT + Extension.JAVA, mainJavaConfig.getFileNameEx());
    assertNotNull(mainJavaConfig.getCodeBlocks());
    assertEquals(5, mainJavaConfig.getCodeBlocks().size());

    Config appBuildGradleConfig = faModule.getConfigs().get(1);
    assertEquals(AskJson.APP_PATH_REPLACEMENT, appBuildGradleConfig.getPath());
    assertEquals(BuildGradle.FILE_NAME, appBuildGradleConfig.getFileNameEx());
    assertNotNull(appBuildGradleConfig.getCodeBlocks());
    assertEquals(1, appBuildGradleConfig.getCodeBlocks().size());
  }

  @Test
  public void commandFirebaseCrashreportingTest() throws Exception {
    Module fcModule = askJson.getModuleByCommand("fc");
    assertNotNull(fcModule);
    assertNotNull(fcModule.getClassNames());
    assertEquals(1, fcModule.getClassNames().size());
    assertEquals("FireBaseCrashReport", fcModule.getClassNames().get(0));

    assertNotNull(fcModule.getCommands());
    assertEquals(2, fcModule.getCommands().size());
    assertEquals(false, fcModule.getCommands().contains("firebasecrashreport"));
    assertEquals(false, fcModule.getCommands().contains("fa"));
    assertEquals(true, fcModule.getCommands().contains("fc"));
    assertEquals(true, fcModule.getCommands().contains("firebasecrashreporting"));

    assertNotNull(fcModule.getConfigs());
    assertEquals(2, fcModule.getConfigs().size());

    Config mainJavaConfig = fcModule.getConfigs().get(0);
    assertEquals(AskJson.JAVA_PATH_REPLACEMENT, mainJavaConfig.getPath());
    assertEquals(AskJson.MAIN_ACTIVITY_REPLACEMENT + Extension.JAVA, mainJavaConfig.getFileNameEx());
    assertNotNull(mainJavaConfig.getCodeBlocks());
    assertEquals(4, mainJavaConfig.getCodeBlocks().size());

    Config appBuildGradleConfig = fcModule.getConfigs().get(1);
    assertEquals(AskJson.APP_PATH_REPLACEMENT, appBuildGradleConfig.getPath());
    assertEquals(BuildGradle.FILE_NAME, appBuildGradleConfig.getFileNameEx());
    assertNotNull(appBuildGradleConfig.getCodeBlocks());
    assertEquals(1, appBuildGradleConfig.getCodeBlocks().size());
  }
}
