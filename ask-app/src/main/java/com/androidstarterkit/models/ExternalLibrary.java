package com.androidstarterkit.models;

import com.androidstarterkit.config.SyntaxConfig;

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
    private static final String USES_PERMISSION = "<uses-permission android:name=\"android.permission."
        + SyntaxConfig.REPLACE_STRING + "\"/>";

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

    public String[] getUsesPermissions() {
      if (permissions == null) {
        return null;
      }

      String[] permissionArr = new String[permissions.length];
      for (int i = 0, li = permissions.length; i < li; i++) {
        permissionArr[i] = USES_PERMISSION.replace(SyntaxConfig.REPLACE_STRING, permissions[i].name());
      }
      return permissionArr;
    }
  }

}
