package exception;

/**
 * malformed http message
 */
public class MalformedException extends Exception {
    public MalformedException(String message) {
        super(message);
    }
}
