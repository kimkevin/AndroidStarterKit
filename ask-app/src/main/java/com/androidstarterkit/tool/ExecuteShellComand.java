package com.androidstarterkit.tool;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecuteShellComand {
  public static String executeCommand(String command, boolean isConsolePrinted) {
    StringBuilder output = new StringBuilder();
    try {
      Process proc = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

      String line;
      while ((line = reader.readLine())!= null) {
        output.append(line + "\n");

        if (isConsolePrinted) {
          System.out.println(line);
        }
      }
      proc.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return output.toString();
  }

  public static void executeAssemble(String pathname, boolean isConsolePrinted) {
    try {
      Process proc = Runtime.getRuntime().exec(pathname);
      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

      String line;
      while ((line = reader.readLine())!= null) {
        if (isConsolePrinted) {
          System.out.print("\r" + line);
        }
      }
      proc.waitFor();
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


