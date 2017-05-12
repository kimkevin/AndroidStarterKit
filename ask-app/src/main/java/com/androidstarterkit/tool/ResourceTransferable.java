package com.androidstarterkit.tool;


import com.androidstarterkit.directory.RemoteDirectory;

import java.io.FileNotFoundException;

public interface ResourceTransferable {
  void transferResourceFileFromRemote(RemoteDirectory remoteDirectory, String resourceTypeName
      , String layoutName
      , int depth) throws FileNotFoundException;

  void transferValueFromRemote(RemoteDirectory remoteDirectory, String resourceTypeName
      , String valueName
      , int depth) throws FileNotFoundException;
}
