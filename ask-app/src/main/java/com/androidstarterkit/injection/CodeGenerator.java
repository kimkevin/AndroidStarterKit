package com.androidstarterkit.injection;


import com.androidstarterkit.injection.model.Config;

import java.util.List;

public interface CodeGenerator<T extends Config> {
  void addConfig(T config);
  void addConfig(List<T> configs);
  List<T> getConfigs();
  void apply();
}
