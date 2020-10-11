/**
 * An exception to indicate that a method unexpectedly reached the end of the
 * input stream.
 */
class EofException extends RuntimeException {
    public EofException() {}

    public EofException(String message) {
        super(message);
    }

    public EofException(String message, Throwable cause) {
        super(message, cause);
    }

    public EofException(Throwable cause) {
        super(cause);
    }
}