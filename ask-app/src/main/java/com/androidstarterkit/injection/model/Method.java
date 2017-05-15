package com.androidstarterkit.injection.model;


import com.androidstarterkit.util.SyntaxUtils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Method {
  @SerializedName("class")
  private String classname;

  @SerializedName("name")
  private String name;

  @SerializedName("annotation")
  private List<String> annotations;

  @SerializedName("access_modifier")
  private String accessModifier;

  @SerializedName("return_type")
  private String returnType;

  @SerializedName("parameter")
  private List<Parameter> parameters;

  @SerializedName("lines")
  private List<String> lines;

  private boolean isImported;

  public class Parameter {
    @SerializedName("type")
    private String type;

    @SerializedName("variable")
    private String variable;

    @Override
    public String toString() {
      return "Parameter{" +
          "type='" + type + '\'' +
          ", variable='" + variable + '\'' +
          '}';
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getLines() {
    return lines;
  }

  public boolean isImported() {
    return isImported;
  }

  public void setImported(boolean imported) {
    isImported = imported;
  }

  public List<String> createMethodAsString(int indentCount) {
    List<String> result = new ArrayList<>();
    result.add("");

    result.addAll(annotations.stream()
        .map(annotation -> SyntaxUtils.createIndentAsString(indentCount) + annotation)
        .collect(Collectors.toList()));

    String newCodeline = SyntaxUtils.createIndentAsString(indentCount);
    if (accessModifier != null) {
      newCodeline += accessModifier + " ";
    }

    if (returnType != null) {
      newCodeline += returnType + " ";
    }

    if (name != null) {
      newCodeline += name;
    }

    newCodeline += "(";
    if (parameters != null) {
      for (int i = 0, li = parameters.size(); i < li; i++) {
        newCodeline += parameters.get(i).type + " " + parameters.get(i).variable;
        if (i < li - 1) {
          newCodeline += ", ";
        }
      }
    }

    newCodeline += ") {";
    result.add(newCodeline);

    if (annotations.contains("@Override")) {
      newCodeline = SyntaxUtils.createIndentAsString(indentCount + 1) + "super." + name + "(";
      for (int i = 0, li = parameters.size(); i < li; i++) {
        newCodeline += parameters.get(i).variable;
        if (i < li - 1) {
          newCodeline += ", ";
        }
      }
      newCodeline += ");";
      result.add(newCodeline);
    }

    result.addAll(lines.stream()
        .map(line -> SyntaxUtils.createIndentAsString(indentCount + 1) + line)
        .collect(Collectors.toList()));

    result.add(SyntaxUtils.createIndentAsString(indentCount) + "}");

    return result;
  }

  @Override
  public String toString() {
    return "Method{" +
        "classname='" + classname + '\'' +
        ", name='" + name + '\'' +
        ", annotations=" + annotations +
        ", accessModifier='" + accessModifier + '\'' +
        ", returnType='" + returnType + '\'' +
        ", parameters=" + parameters +
        ", lines=" + lines +
        ", isImported=" + isImported +
        '}';
  }
}
