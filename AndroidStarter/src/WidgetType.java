public enum WidgetType {
  RecyclerView("RecyclerView"),
  ListView("ListView"),
  SlidingTabLayout("SlidingTabLayout"),
  SlidingIconTabLayout("SlidingIconTabLayout");

  private String fileName;

  WidgetType(String fileName) {
    this.fileName = fileName;
  }

  public String getName() throws UnsupportedWidgetTypeException {
    if (fileName == null) {
      throw new UnsupportedWidgetTypeException("Filed : Not supported widget type");
    }

    return fileName;
  }

  public String getActivityName() throws UnsupportedWidgetTypeException {
    if (fileName == null) {
      throw new UnsupportedWidgetTypeException("Filed : Not supported widget type");
    }

    return fileName + "Activity.java";
  }
}
