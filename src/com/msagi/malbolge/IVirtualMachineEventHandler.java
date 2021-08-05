package com.msagi.malbolge;

/**
 * Event handler interface for the Malbolge virtual machine.
 * @author msagi
 */
public interface IVirtualMachineEventHandler {

    /**
     * 'Program decrypted' event handler.
     * @param source The list of Malbolge source (decrypted) instructions of the program.
     */
    void onProgramDecrypted(final int[] source);

    /**
     * 'Execute started' event handler.
     */
    void onExecuteStarted();

    /**
     * 'Instruction skipped' event handler.
     * @param registerC Value of the register C.
     * @param registerCAddress Value of the register C memory address.
     */
    void onInstructionSkipped(int registerC, int registerCAddress);

    /**
     * 'Instruction skipped' event handler.
     * @param registerA Value of the register A.
     * @param registerC Value of the register C.
     * @param registerCAddress Value of the register C memory address.
     * @param registerD Value of the register D.
     * @param registerDAddress Value of the register D memory address.
     * @param instruction The instruction code.
     */
    void onInstructionExecute(int registerA, int registerC, int registerCAddress, int registerD, int registerDAddress, int instruction);

    /**
     * 'Execute finished' event handler.
     */
    void onExecuteFinished();
}
