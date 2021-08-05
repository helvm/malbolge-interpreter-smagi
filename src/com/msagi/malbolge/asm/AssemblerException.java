package com.msagi.malbolge.asm;

/**
 * Created by msagi on 16/05/15.
 */
public class AssemblerException extends Exception {

    /**
     * Create new instance with given message.
     * @param message The message to use.
     */
    AssemblerException(final String message) {
        super(message);
    }
}
