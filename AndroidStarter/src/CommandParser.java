import java.util.*;

public class CommandParser {

  private List<String> argList;

  public CommandParser(String[] args) {
    argList = new ArrayList<>(Arrays.asList(args));
  }

  public String getPath() throws CommandParseException {
    final String option = findOption(CommandOption.PATH_KEY, CommandOption.PATH_LONG_KEY);

    if (option == null) {
      throw new CommandParseException("Missing a project path : please check -p <path>");
    }
    return option;
  }

  public WidgetType getWidgetType() throws CommandParseException, UnsupportedWidgetTypeException {
    final String typeStr = findOption(CommandOption.WIDGET_KEY, CommandOption.WIDGET_LONG_KEY);

    if (typeStr == null) {
      throw new CommandParseException("Missing a widget type : please check -w <widget>");
    }

    if (typeStr.equals(WidgetType.RecyclerView.getName())) {
      return WidgetType.RecyclerView;
    } else if (typeStr.equals(WidgetType.ListView.getName())) {
      return WidgetType.ListView;
    } else {
      throw new CommandParseException("Unsupported a widget type : please check -h , --help");
    }
  }

  private String findOption(String... key) {
    for (int i = 0, li = argList.size(); i < li; i++) {
      if (contain(argList.get(i), key)) {
        if (i < argList.size() - 1) {
          return argList.get(i + 1);
        }
      }
    }

    return null;
  }

  private boolean contain(String arg, String... key) {
    for (int i = 0, li = key.length; i < li; i++) {
      if (arg.equals(key[i])) {
        return true;
      }
    }

    return false;
  }
}
