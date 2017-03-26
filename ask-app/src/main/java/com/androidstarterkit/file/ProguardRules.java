package com.androidstarterkit.file;


import com.androidstarterkit.model.CodeBlock;
import com.androidstarterkit.tool.CodeGenerator;

import java.util.List;

public class ProguardRules extends BaseFile implements CodeGenerator {

  public static final String FILE_NAME = "proguard-rules.pro";

  private List<CodeBlock> configCodeBlocks;

  public ProguardRules(String pathName) {
    super(pathName, FILE_NAME);
  }


  @Override
  public void addCodeBlock(CodeBlock codeBlock) {

  }

  @Override
  public void setCodeBlocks(List<CodeBlock> codeBlocks) {
    configCodeBlocks = codeBlocks;
  }

  @Override
  public void apply() {

  }
}
