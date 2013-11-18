package common;

/**
 * This RuntimeException is throw when something, that should not have happend,
 * happend and the compiler can't continue it's job
 */
public class InternalCompilerErrorRuntimeException extends RuntimeException {

    /**
     * Serial Version UID for Java Serialization
     */
    private static final long serialVersionUID = -1070755539845099878L;

    /**
     * Creates a new InternalCompilerErrorRuntimeException
     *  
     */
    public InternalCompilerErrorRuntimeException() {
        super();
    }

    /**
     * Creates a new InternalCompilerErrorRuntimeException
     * 
     * @param message
     *            set message to this
     */
    public InternalCompilerErrorRuntimeException(final String message) {
        super(message);
    }
}
