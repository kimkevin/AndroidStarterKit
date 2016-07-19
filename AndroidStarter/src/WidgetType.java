public enum WidgetType {
  RecyclerView, ListView, SlidingTabLayout, SlidingIconTabLayout;

  public String getFileName() throws UnsupportedWidgetTypeException {
    int index = ordinal();

    if (index == RecyclerView.ordinal()) {
      return FileNames.RECYCLERVIEW_ACTIVITY;
    } else if (index == ListView.ordinal()) {
      return FileNames.LISTVIEW_ACTIVITY;
    } else {
      throw new UnsupportedWidgetTypeException("Filed : Not supported widget type");
    }
  }
}
