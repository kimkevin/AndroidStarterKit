import java.io.File;
import java.util.List;

public class BuildGradleFile extends File{
  public static final String FILE_NAME = "build.gradle";

  private List<String> lineList;

  public BuildGradleFile(String pathname) {
    super(pathname + "/" + FILE_NAME);

    lineList = FileUtils.readFile(this);
  }

  /**
   * Add dependencies on compile configuration
   *
   * @param externalLibraries is strings
   */
  public void addDependency(String... externalLibraries) {
    if (externalLibraries.length <= 0) {
      return;
    }

    for (int i = 0, li = externalLibraries.length; i < li; i++) {
      lineList = FileUtils.addLineToObject(BuildGradleConfig.DEPENDENCIES_ELEMENT_NAME,
              BuildGradleConfig.COMPILE_CONFIGURATION_FORMAT.replace(SyntaxConfig.REPLACE_STRING,
                      externalLibraries[i]),
              lineList);
    }

    FileUtils.writeFile(this, FileUtils.getString(lineList));
  }
}
