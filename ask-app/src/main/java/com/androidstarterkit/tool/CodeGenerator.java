package com.androidstarterkit.tool;


import com.androidstarterkit.model.CodeBlock;

import java.util.List;

public interface CodeGenerator {
  void addCodeBlock(CodeBlock codeBlock);
  void setCodeBlocks(List<CodeBlock> codeBlocks);
  void apply();
}
