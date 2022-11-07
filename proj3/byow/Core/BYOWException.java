package byow.Core;

public class BYOWException extends RuntimeException {
    /** A BYOWException with no message. */
    BYOWException() {
        super();
    }

    /** A BYOWException MSG as its message. */
    BYOWException(String msg) {
        super(msg);
    }
}
