package com.androidstarterkit.injection.file.base;

import com.androidstarterkit.injection.model.CodeBlock;
import com.androidstarterkit.injection.CodeGenerator;
import com.androidstarterkit.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InjectionBaseFile extends File implements CodeGenerator {
  protected List<String> codelines;
  protected List<CodeBlock> configCodeBlocks;

  public InjectionBaseFile(String pathname) {
    super(pathname);
    configCodeBlocks = new ArrayList<>();

    codelines = FileUtils.readFile(this);
  }

  public List<String> getCodelines() {
    return codelines;
  }

  public void print() {
    for (String line : codelines) {
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
    FileUtils.writeFile(this, codelines);
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
    for (String codeline : codelines) {
      if (codeline.trim().equals(newCodeline)) {
        return true;
      }
    }
    return false;
  }
}
