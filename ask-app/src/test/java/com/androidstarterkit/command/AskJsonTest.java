package com.androidstarterkit.command;


import com.androidstarterkit.config.AskConfig;
import com.androidstarterkit.exception.CommandException;
import com.androidstarterkit.injection.AskJson;
import com.androidstarterkit.injection.model.LayoutGroup;
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
      askJson = gson.fromJson(FileUtils.readFile(FileUtils.linkPathWithSlash(FileUtils.getRootPath()
          , AskConfig.DEFAULT_ASK_APP_NAME, AskJson.FILE_NAME)), AskJson.class);
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

//  @Test
//  public void getModuleGroupByCommandTest() throws Exception {
//    ModuleGroup moduleGroup = askJson.getModuleGroupByCommand("gv");
//    assertEquals(null, moduleGroup);
//
//    moduleGroup = askJson.getModuleGroupByCommand("fa");
//    assertNotNull(moduleGroup);
//    assertNotNull(moduleGroup.getName());
//    assertNotNull(moduleGroup.getConfigFilenames());
//    assertEquals(1, moduleGroup.getConfigFilenames().size());
//    assertEquals(2, moduleGroup.getModules().size());
//    assertEquals(2, moduleGroup.getGroupGradleConfigs().size());
//
//    for (GradleConfig config : moduleGroup.getGroupGradleConfigs()) {
//      assertNotNull(config.getClassname());
//      assertNotNull(config.getFileNameEx());
//      assertNotNull(config.getSnippets());
//    }
//  }
//
//  @Test
//  public void commandFirebaseAnalyticsTest() throws Exception {
//    Module faModule = askJson.getModuleByCommand("fa");
//    assertNotNull(faModule);
//    assertNotNull(faModule.getClassNames());
//    assertEquals(1, faModule.getClassNames().size());
//    assertEquals("FireBaseAnalytics", faModule.getClassNames().get(0));
//
//    assertNotNull(faModule.getCommands());
//    assertEquals(2, faModule.getCommands().size());
//    assertEquals(true, faModule.getCommands().contains("firebaseanalytics"));
//    assertEquals(true, faModule.getCommands().contains("fa"));
//    assertEquals(false, faModule.getCommands().contains("firebaseanalytic"));
//
//    assertNotNull(faModule.getConfigs());
//    assertEquals(2, faModule.getConfigs().size());
//
//    GradleConfig mainJavaConfig = faModule.getConfigs().get(0);
//    assertEquals(AskJson.JAVA_PATH_REPLACEMENT.replace("\\", ""), mainJavaConfig.getClassname());
//    assertEquals(AskJson.MAIN_ACTIVITY_REPLACEMENT.replace("\\", "") + Extension.JAVA, mainJavaConfig.getFileNameEx());
//    assertNotNull(mainJavaConfig.getSnippets());
//    assertEquals(5, mainJavaConfig.getSnippets().size());
//
//    GradleConfig appBuildGradleConfig = faModule.getConfigs().get(1);
//    assertEquals(AskJson.APP_PATH_REPLACEMENT.replace("\\", ""), appBuildGradleConfig.getClassname());
//    assertEquals(BuildGradle.FILE_NAME, appBuildGradleConfig.getFileNameEx());
//    assertNotNull(appBuildGradleConfig.getSnippets());
//    assertEquals(1, appBuildGradleConfig.getSnippets().size());
//  }
//
//  @Test
//  public void commandFirebaseCrashreportingTest() throws Exception {
//    Module fcModule = askJson.getModuleByCommand("fc");
//    assertNotNull(fcModule);
//    assertNotNull(fcModule.getClassNames());
//    assertEquals(1, fcModule.getClassNames().size());
//    assertEquals("FireBaseCrashReport", fcModule.getClassNames().get(0));
//
//    assertNotNull(fcModule.getCommands());
//    assertEquals(2, fcModule.getCommands().size());
//    assertEquals(false, fcModule.getCommands().contains("firebasecrashreport"));
//    assertEquals(false, fcModule.getCommands().contains("fa"));
//    assertEquals(true, fcModule.getCommands().contains("fc"));
//    assertEquals(true, fcModule.getCommands().contains("firebasecrashreporting"));
//
//    assertNotNull(fcModule.getConfigs());
//    assertEquals(2, fcModule.getConfigs().size());
//
//    GradleConfig mainJavaConfig = fcModule.getConfigs().get(0);
//    assertEquals(AskJson.JAVA_PATH_REPLACEMENT.replace("\\", ""), mainJavaConfig.getClassname());
//    assertEquals(AskJson.MAIN_ACTIVITY_REPLACEMENT.replace("\\", "") + Extension.JAVA, mainJavaConfig.getFileNameEx());
//    assertNotNull(mainJavaConfig.getSnippets());
//    assertEquals(4, mainJavaConfig.getSnippets().size());
//
//    GradleConfig appBuildGradleConfig = fcModule.getConfigs().get(1);
//    assertEquals(AskJson.APP_PATH_REPLACEMENT.replace("\\", ""), appBuildGradleConfig.getClassname());
//    assertEquals(BuildGradle.FILE_NAME, appBuildGradleConfig.getFileNameEx());
//    assertNotNull(appBuildGradleConfig.getSnippets());
//    assertEquals(1, appBuildGradleConfig.getSnippets().size());
//  }
}
