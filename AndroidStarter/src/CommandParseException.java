import java.text.ParseException;

public class CommandParseException extends Exception{
  public CommandParseException() {
  }

  public CommandParseException(String s) {
    super(s);
  }
}
