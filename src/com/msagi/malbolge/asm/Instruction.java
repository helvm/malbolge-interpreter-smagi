package com.msagi.malbolge.asm;

/**
 * The list of Malbolge instructions.
 *
 * @author msagi
 */
public enum Instruction {

    MOV_D_ADDRD("MOV D, [D]", 'j'),
    MOV_C_ADDRD("MOV C, [D]", 'i'),
    ROTR_ADDRD_MOV_A_ADDRD("ROTR [D]; MOV A, [D]", '*'),
    CRZ_A_ADDRD_MOV_A_ADDRD("CRZ A, [D]; MOV A, [D]", 'p'),
    OUT_A("OUT A", '<'),
    IN_A("IN A", '/'),
    NOP("NOP", 'o'),
    EXIT("EXIT", 'v'),;

    /**
     * The assembly source code of the instruction.
     */
    private final String asm;
    /**
     * The binary operation code of the instruction.
     */
    private final char opCode;

    /**
     * Create new instance.
     *
     * @param asm    The assembly to use.
     * @param opCode The binary to use.
     */
    Instruction(final String asm, final char opCode) {
        this.asm = asm;
        this.opCode = opCode;
    }

    public int getOpCode() {
        return opCode;
    }

    /**
     * Get the instruction by the source code.
     *
     * @param sourceCode The source code to analyse.
     * @return The matching instruction or null if not any.
     */
    public static Instruction getByAssembly(final String sourceCode) {
        if (sourceCode == null) {
            return null;
        }

        for (final Instruction instruction : Instruction.values()) {
            if (instruction.asm.equalsIgnoreCase(sourceCode)) {
                return instruction;
            }
        }

        return null;
    }
}
