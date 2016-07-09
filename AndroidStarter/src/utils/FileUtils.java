package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {
  public static final String TAG = FileUtils.class.getSimpleName();

  public static File getFileInDirectory(String dirPath, String fileName) {
    File dir = new File(dirPath);
    File file = new File(dir, fileName);
    return file;
  }

  public static String makePathWithSlash(String... args) {
    String path = "";
    for (int i = 0, li = args.length; i < li; i++) {
      path += args[i];

      if (i != li - 1) {
        path += "/";
      }
    }
    return path;
  }

  public static void copyFile(String sourcePath, String destPath, String fileName) throws IOException {
    File sourceFile = new File(makePathWithSlash(sourcePath, fileName));
    File destFile = new File(makePathWithSlash(destPath, fileName));

    if (!sourceFile.exists()) {
      return;
    }

    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel sourceChannel;
    FileChannel destinationChannel;

    sourceChannel = new FileInputStream(sourceFile).getChannel();
    destinationChannel = new FileOutputStream(destFile).getChannel();

    if (destinationChannel != null && sourceChannel != null) {
      destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    }

    if (sourceChannel != null) {
      sourceChannel.close();
    }

    if (destinationChannel != null) {
      destinationChannel.close();
    }
  }
}
