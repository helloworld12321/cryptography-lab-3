import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * This class contains some utility methods for making nice text menus on the
 * terminal.
 */
public class MenuSystem {
    private PrintStream out;
    private Scanner scanner;

    /**
     * Create a new menu system that writes to a given OutputStream and reads
     * input from a given InputStream.
     */
    public MenuSystem(PrintStream output, InputStream input) {
        this.out = output;
        this.scanner = new Scanner(input);
    }

    /**
     * Write a message to the user without reading any input back.
     *
     * (This is just an alias of `OutputStream.println()`.)
     */
    public void message(String message) {
        out.println(message);
    }

    /**
     * Give the user a choice between several options.
     *
     * @param message Instructions, a prompt, or header text; displayed before
     * before the list of options.
     *
     * @param options A list of option names, which we'll show to the user.
     *
     * @return The index of the option chosen (as listed in the `options`
     * array.)
     *
     * @throws EofException if the user closes the input stream before
     * choosing an option.
     */
    public int choice(String message, String[] options) {
        out.println(message);
        for (int i = 0; i < options.length; i++) {
            // Print out the options indexed from 1 rather than 0, for the sake
            // of being human-friendly.
            out.printf("%d) %s\n", i + 1, options[i]);
        }

        String line;
        while (true) {
            String input;
            do {
                out.print("? ");
                try {
                    input = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    throw new EofException(e);
                }
                // If the user just typed an empty line, prompt them again.
                // (They probably hit enter accidentally.)
            } while (input.isEmpty());

            // Hack to fix the "local variables in lambdas must be effectively
            // final" error. (Technically, you don't need the final modifier
            // here; it's sufficient to use a different variable name. I'm
            // just declaring it final for clarity.)
            final String theInput = input;

            int inputAsANumber;
            try {
                inputAsANumber = Integer.parseInt(theInput.trim());
            } catch (NumberFormatException e) {
                // The user typed in something that wasn't an integer.
                // Just in case, check to see if it matches the text of one of
                // the options.
                Predicate<String> matchesInput = candidate ->
                    candidate.trim().equalsIgnoreCase(theInput.trim());

                if (Arrays.stream(options).anyMatch(matchesInput)) {
                    return IntStream.range(0, options.length)
                        .filter(i -> matchesInput.test(options[i]))
                        .findFirst()
                        .getAsInt();
                } else {
                    out.println("Sorry, that's not one of the options.");
                    out.println("Please try again:");
                    continue;
                }
            }

            if (
                    inputAsANumber - 1 < 0
                    || inputAsANumber - 1 >= options.length) {
                // The user typed in an integer, but it wasn't
                // one of the options listed.
                out.println("Sorry, that's not one of the options.");
                out.print("Try again? ");
                continue;
            }

            return inputAsANumber - 1;
        }
    }

    /**
     * Get a (non-empty) line of text from the user.
     *
     * @param validators A list of validators to run against the value the user
     * inputs. If any of the validators fail, the user will be asked to input
     * something else.
     *
     * @throws EofException if the user closes the input stream before
     * choosing an option.
     */
    public String getLine(String message, Validator<String>[] validators) {
        out.println(message);

        String line;
        do {
            out.print("? ");
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                throw new EofException(e);
            }
            // If the user just typed an empty line, prompt them again.
            // (They probably hit enter accidentally.)
        } while (line.isEmpty());

        while (!allValidate(validators, line)) {
            final String theLine = line;

            Validator<String> firstFailingValidator = Arrays.stream(validators)
                .filter(v -> !v.predicate.test(theLine))
                .findFirst()
                .get();
            System.out.println(firstFailingValidator.message);
            System.out.println("Please enter something else:");
            do {
                out.print("? ");
                try {
                    line = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    throw new EofException(e);
                }
            } while (line.isEmpty());
        }

        return line;
    }

    private static <T> boolean allValidate(Validator<T>[] validators, T value) {
        return Arrays.stream(validators)
            .allMatch(validator -> validator.predicate.test(value));
    }
}