package com.androidstarterkit.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExternalLibrary {
  protected Map<String, Info> dictionary;

  public ExternalLibrary(String supportLibraryVersion) {
    dictionary = new HashMap<>();
    dictionary.put("CardView", new Info("com.android.support:cardview-v7:" + supportLibraryVersion));
    dictionary.put("RecyclerView", new Info("com.android.support:recyclerview-v7:" + supportLibraryVersion));
    dictionary.put("Glide", new Info("com.github.bumptech.glide:glide:3.7.0", Permission.INTERNET));
  }

  public Set<String> getKeys() {
    return dictionary.keySet();
  }

  public Info getInfo(String key) {
    return dictionary.get(key);
  }

  public static class Info {
    private String library;
    private Permission[] permissions;

    public Info(String library) {
      this.library = library;
    }

    public Info(String library, Permission... permissions) {
      this.library = library;
      this.permissions = permissions;
    }

    public String getLibrary() {
      return library;
    }

    public Permission[] getPermissions() {
      return permissions;
    }
  }
}
