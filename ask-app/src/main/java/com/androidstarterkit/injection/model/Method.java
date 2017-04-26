package com.androidstarterkit.injection.model;


import com.androidstarterkit.constraints.SyntaxConstraints;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<String> annotations) {
    this.annotations = annotations;
  }

  public String getAccessModifier() {
    return accessModifier;
  }

  public void setAccessModifier(String accessModifier) {
    this.accessModifier = accessModifier;
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
  }

  public List<String> getLines() {
    return lines;
  }

  public void setLines(List<String> lines) {
    this.lines = lines;
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
        '}';
  }

  public String createMethodAsString() {
    String result = "";
    for (String annotation : annotations) {
      result += annotation + "\n";
    }

    if (accessModifier != null) {
      result += accessModifier + " ";
    }

    if (returnType != null) {
      result += returnType + " ";
    }

    if (name != null) {
      result += name;
    }

    result += "(";
    for (int i = 0, li = parameters.size(); i < li; i++) {
      result += parameters.get(i).type + " " + parameters.get(i).variable;
      if (i < li - 1) {
        result += ", ";
      }
    }
    result += ") {\n";
    if (annotations.contains("@Override")) {
      result += SyntaxConstraints.DEFAULT_INDENT + "super." + name + "(";
      for (int i = 0, li = parameters.size(); i < li; i++) {
        result += parameters.get(i).variable;
        if (i < li - 1) {
          result += ", ";
        }
      }
      result += ");\n";
    }
    result += "}\n";

    return result;
  }
}
