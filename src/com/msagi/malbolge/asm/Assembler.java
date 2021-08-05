package com.msagi.malbolge.asm;

import com.msagi.malbolge.Utils;
import com.msagi.malbolge.VirtualMachine;

import java.io.*;

/**
 * Assembler for Malbolge Assembly source code.
 *
 * @author msagi
 */
public class Assembler {

    /**
     * The syntax help text.
     */
    private static final String SYNTAX = "" +
            "Malbolge assembly language syntax:\n" +
            "\t- one instruction per line\n" +
            "\t- instructions are case insensitive\n" +
            "\t- empty line is ignored\n" +
            "\t- single line comment marker is \"//\", everything is ignored after this in a line\n" +
            "\t- list of instructions:\n" +
            "\t\tMOV D, [D]: move the value from the memory address of register D to register D\n" +
            "\t\tMOV C, [D]: move the value from the memory address of register D to register C (e.g. jump)\n" +
            "\t\tROTR [D]; MOV A, [D]: rotate the value at the memory address of register D then move it to register A\n" +
            "\t\tCRZ A, [D]; MOV A, [D]: set the value at the memory address of register D using CRZ operation then move it to register A\n" +
            "\t\tOUT A: print the value of register A to the output\n" +
            "\t\tIN A: read one character from the input and move its value to register A\n" +
            "\t\tNOP: no-operation\n" +
            "\t\tEXIT: end of the program";

    /**
     * The default assembler flag.
     */
    public static final int FLAG_DEFAULT = 0;

    /**
     * Flag for the assembler: not to encrypt the binary code after assembling.
     */
    public static final int FLAG_ASSEMBLE_WITHOUT_ENCRYPTION = 1;

    /**
     * Flag for the assembler: not to put line breaks to the binary code after each 32 bytes.
     */
    public static final int FLAG_ASSEMBLE_WITHOUT_LINE_BREAKS = 2;

    /**
     * The comment token of a line.
     */
    private static final String COMMENT_TOKEN = "//";

    /**
     * Assemble a Malbolge assembly source code to Malbolge binary code.
     *
     * @param in    The input stream with the assembly program.
     * @param out   The output stream with the binary code.
     * @param flags The compiler flags. Use bitwise OR to set multiple flags.
     * @see #FLAG_ASSEMBLE_WITHOUT_ENCRYPTION
     * @see #FLAG_ASSEMBLE_WITHOUT_LINE_BREAKS
     */
    public void assemble(final InputStream in, final OutputStream out, final int flags) throws AssemblerException {
        if (in == null) {
            throw new IllegalArgumentException("in == null");
        }
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        try {
            int byteIndex = 0;
            int lineIndex = 0;
            final BufferedReader lines = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = lines.readLine()) != null) {

                lineIndex++;

                if (byteIndex >= VirtualMachine.MEMORY_SIZE - 1) {
                    throw new AssemblerException("Program cannot fit into memory");
                }

                final int commentIndex = line.indexOf(COMMENT_TOKEN);

                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex);
                }

                line = line.trim();

                if ("".equals(line)) {
                    continue;
                }

                final Instruction instruction = Instruction.getByAssembly(line);
                if (instruction == null) {
                    final String message = String.format("Syntax error in line %d, unknown instruction: '%s'", lineIndex, line);
                    throw new AssemblerException(message);
                }

                if ((flags & FLAG_ASSEMBLE_WITHOUT_ENCRYPTION) == FLAG_ASSEMBLE_WITHOUT_ENCRYPTION) {
                    out.write(instruction.getOpCode());
                } else {
                    int encryptedOpCode = 0;
                    for (int index = 33; index < 127; index++) {
                        final int xlat1Index = (index - 33 + byteIndex) % 94;
                        final int opCode = Utils.xlat1.charAt(xlat1Index);
                        if (opCode == instruction.getOpCode()) {
                            encryptedOpCode = index;
                            out.write(encryptedOpCode);
                            break;
                        }
                    }
                    if (encryptedOpCode == 0) {
                        final String message = String.format("Syntax error in line %d, cannot encrypt instruction: '%s'", lineIndex, line);
                        throw new AssemblerException(message);
                    }
                }

                if (byteIndex % 32 == 31 && (flags & FLAG_ASSEMBLE_WITHOUT_LINE_BREAKS) == 0) {
                    out.write(10);
                }

                byteIndex++;
            }
            out.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Main method of the Malbolbe assembler.
     * @param args The array of command line arguments.
     * @see #SYNTAX
     */
    public static void main(final String[] args) {

        if (args.length == 0) {
            System.out.println(SYNTAX);
            System.exit(-1);
        }

        if (args.length == 1 || args.length == 2) {
            final Assembler assembler = new Assembler();
            try {
                final InputStream in = new FileInputStream(args[0]);
                OutputStream out = System.out;
                if (args.length == 2) {
                    out = new FileOutputStream(args[1]);
                }
                assembler.assemble(in, out, FLAG_DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-2);
            }
        } else {
            System.out.println("Usage: Assembler sourceFilePath [binaryFilePath]");
        }
    }
}
