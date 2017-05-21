package com.androidstarterkit.tool;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavaLineScannerTest {
  private JavaLineScanner javaLineScanner;
  private List<String> CLASS_SAMPLE_1 = Arrays.asList(
      "package com.androidstarterkit.sample;",
      "",
      "import android.os.Bundle;",
      "import android.support.v7.app.AppCompatActivity;",
      "",
      "public class MainActivity extends AppCompatActivity {",
      "",
      "  @Override",
      "  protected void onCreate(Bundle savedInstanceState) {",
      "    super.onCreate(savedInstanceState);",
      "    setContentView(R.layout.activity_main);",
      "  }",
      "}"
  );

  private List<String> CLASS_SAMPLE_2 = Arrays.asList(
      "package com.androidstarterkit.sample;",
      "",
      "import android.os.Bundle;",
      "import android.support.v7.app.AppCompatActivity;",
      "",
      "public class MainActivity extends AppCompatActivity {",
      "",
      "  @Override",
      "  protected void onCreate(Bundle savedInstanceState) {",
      "    super.onCreate(savedInstanceState);",
      "    setContentView(R.layout.activity_main);",
      "  }",
      "} ",
      "// last close bracket }"
  );

  @Test
  public void getClostBracketIndexTest() throws Exception {
    javaLineScanner = new JavaLineScanner();
    assertEquals(12, javaLineScanner.getCloseBracketIndex(CLASS_SAMPLE_1));
    assertEquals(12, javaLineScanner.getCloseBracketIndex(CLASS_SAMPLE_2));
  }
}
