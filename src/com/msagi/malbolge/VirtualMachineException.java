package com.msagi.malbolge;

/**
 * Custom exception class for the Malbolge Virtual Machine.
 * @author msagi
 */
public class VirtualMachineException extends Exception {

    /**
     * Create new instance with default message.
     */
    public VirtualMachineException() {
        super("Malbolge virtual machine exception");
    }

    /**
     * Create new instance by wrapping a cause exception.
     * @param cause The cause exception to be wrapped.
     */
    public VirtualMachineException(final Exception cause) {
        super(cause);
    }

    /**
     * Create new instance with given message.
     * @param message The exception message.
     */
    public VirtualMachineException(final String message) {
        super(message);
    }
}
