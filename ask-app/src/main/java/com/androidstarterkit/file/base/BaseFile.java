package com.androidstarterkit.file.base;

import com.androidstarterkit.model.CodeBlock;
import com.androidstarterkit.tool.CodeGenerator;
import com.androidstarterkit.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseFile extends File implements CodeGenerator {
  protected List<String> lineList;
  protected List<CodeBlock> configCodeBlocks;

  public BaseFile(String pathname, String filename) {
    this(pathname + "/" + filename);
  }

  public BaseFile(String fullPathname) {
    super(fullPathname);
    configCodeBlocks = new ArrayList<>();

    lineList = FileUtils.readFile(this);
  }

  public List<String> getLineList() {
    return lineList;
  }

  public String getBaseName() {
    return FileUtils.removeExtension(getName());
  }

  public void print() {
    for (String line : lineList) {
      System.out.println(line);
    }
  }

  @Override
  public void addCodeBlock(CodeBlock codeBlock) {
    if (configCodeBlocks == null) {
      configCodeBlocks = new ArrayList<>();
    }

    configCodeBlocks.add(codeBlock);
  }

  @Override
  public void addCodeBlocks(List<CodeBlock> codeBlocks) {
    if (configCodeBlocks == null) {
      configCodeBlocks = new ArrayList<>();
    }

    configCodeBlocks.addAll(codeBlocks);
  }

  @Override
  public List<CodeBlock> getCodeBlocks() {
    return configCodeBlocks;
  }

  @Override
  public void apply() {
    FileUtils.writeFile(this, lineList);
  }

  protected List<String> deduplicatedCodelines(List<String> codelines) {
    List<String> result = new ArrayList<>();
    for (String codeline : codelines) {
      if (!isDuplicatedCodeline(codeline)) {
        result.add(codeline);
      }
    }
    return result;
  }

  private boolean isDuplicatedCodeline(String newCodeline) {
    for (String codeline : lineList) {
      if (codeline.trim().equals(newCodeline)) {
        return true;
      }
    }
    return false;
  }
}
