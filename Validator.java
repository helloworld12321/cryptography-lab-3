import java.util.Arrays;
import java.util.function.Predicate;

/**
 * A validator checks a value to make sure it's good, and has a
 * human-readable error message in case it isn't good.
 *
 * Some helpful pre-defined validators are included.
 */
public class Validator<T> {
    // A predicate that returns true if the value is good, and false otherwise.
    Predicate<T> predicate;
    // A message to display when the value isn't good.
    String message;

    public Validator(Predicate<T> predicate, String message) {
        this.predicate = predicate;
        this.message = message;
    }

    public static Validator<String> isAscii =
        new Validator<>(
            string -> string.matches("^\\p{ASCII}*$"),
            "Sorry, I need a value that only has ASCII characters.");

    public static Validator<String> isBinaryString =
        new Validator<>(
            string -> string.matches("^[01\\s]*$"),
            "Sorry, I need a value that only contains the characters '0' and "
                + "'1' (ignoring whitespace).");

    public static Validator<String> hasLengthIgnoringWhitespace(int length) {
        return new Validator<>(
            string -> string.replaceAll("\\s", "").length() == length,
            String.format(
                "Sorry, I need a string that's %s characters long (ignoring "
                    + "whitespace)",
                length));
    }
}