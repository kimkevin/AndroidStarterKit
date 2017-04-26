package com.androidstarterkit.tool;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteShellComand {
  public static String execute(String command) {
    StringBuffer output = new StringBuffer();

    Process p;
    try {
      p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
      p.waitFor();
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

      String line;
      while ((line = reader.readLine())!= null) {
        output.append(line + "\n");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return output.toString();
  }
}
