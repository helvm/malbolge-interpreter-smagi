package com.msagi.malbolge;

import java.io.*;

/**
 * Malbolge Virtual Machine which is a direct port of Ben Olmstead's original Malbolge interpreter written in C in 1998
 * see http://www.lscheffer.com/malbolge_interp.html
 *
 * @author msagi
 */
public class VirtualMachine {

    /**
     * Virtual machine flag: debug the program execution.
     */
    public static final byte FLAG_WITH_DEBUG = 1;

    /**
     * The maximum value a memory value can have.
     */
    private static final int MAX_VALUE = 59048;

    /**
     * The size of the virtual machine memory (3^10).
     */
    public static final int MEMORY_SIZE = MAX_VALUE + 1;

    /**
     * The virtual machine memory space.
     */
    private int[] memory;

    /**
     * The input stream of the virtual machine.
     */
    private InputStream in;

    /**
     * The output stream of the virtual machine.
     */
    private OutputStream out;

    /**
     * The event machine handler.
     */
    private IVirtualMachineEventHandler eventHandler;

    /**
     * The virtual machine flags.
     */
    private final byte flags;

    /**
     * Create new virtual machine.
     * @param in The input stream og the virtual machine. Cannot be null.
     * @param out The output stream of the virtual machine Cannot be null.
     */
    public VirtualMachine(final InputStream in, final OutputStream out, final byte flags) {
        if (in == null) {
            throw new IllegalArgumentException("in == null");
        }
        this.in = in;
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        this.out = out;
        this.flags = flags;

        if ((flags & FLAG_WITH_DEBUG) == FLAG_WITH_DEBUG) {
            setEventHandler(new DebuggerEventHandler());
        } else {
            setEventHandler(new DefaultEventHandler());
        }
    }

    /**
     * Set the event handler implementation for the Virtual Machine.
     * @param eventHandler The event handler to use. Cannot be null.
     */
    public void setEventHandler(final IVirtualMachineEventHandler eventHandler) {
        if (eventHandler == null) {
            throw new IllegalArgumentException("eventHandler == null");
        }
        this.eventHandler = eventHandler;
    }

    /**
     * Load a Malbolge program into the virtual machine.
     *
     * @param programPath The path of the program file.
     * @throws VirtualMachineException If loading fails.
     */
    public void load(final String programPath) throws VirtualMachineException {

        memory = new int[MEMORY_SIZE];

        final File programFile = new File(programPath);
        if (programFile.isFile()) {

            final FileInputStream in;
            try {
                in = new FileInputStream(programFile);
            } catch (FileNotFoundException fnfe) {
                throw new VirtualMachineException(fnfe);
            }
            int sourceFileIndex = 0;
            int opCodeIndex = 0;
            int encryptedOpCode = -1;
            try {
                while ((encryptedOpCode = in.read()) != -1) {
                    if (Utils.isSpace(encryptedOpCode)) {
                        continue;
                    }
                    int opCode = -1;
                    if (encryptedOpCode < 127 && encryptedOpCode > 32) {
                        final int xlat1Index = (encryptedOpCode - 33 + opCodeIndex) % 94;

                        opCode = Utils.xlat1.charAt(xlat1Index);
                        if (!Utils.isVmInstruction(opCode)) {
                            throw new VirtualMachineException("Invalid character in source file at " + sourceFileIndex + ", code: " + encryptedOpCode + " decrypted opCode: " + (char) opCode);
                        }
                    }
                    if (opCodeIndex == MEMORY_SIZE) {
                        throw new VirtualMachineException("Input file too long");
                    }
                    memory[opCodeIndex++] = encryptedOpCode;
                    sourceFileIndex++;
                }
                in.close();
                System.out.println("Program loaded (instructions: loaded: " + opCodeIndex + ", ignored: " + (sourceFileIndex - opCodeIndex) + "). ");
            } catch (IOException ioe) {
                throw new VirtualMachineException(ioe);
            }

            final int[] programSource = new int[opCodeIndex];
            System.arraycopy(memory, 0, programSource, 0, opCodeIndex);
            eventHandler.onProgramDecrypted(programSource);

            while (opCodeIndex < MEMORY_SIZE) {
                final int crzValue = Utils.crz(memory[opCodeIndex - 1], memory[opCodeIndex - 2]);
                memory[opCodeIndex] = crzValue;
                opCodeIndex++;
            }
        } else {
            System.out.println("Program file not found: " + programFile.getAbsolutePath());
        }
        System.out.println("Ready.");
    }

    /**
     * Execute the previously loaded program.
     *
     * @throws VirtualMachineException If execution fails.
     */
    public void execute() throws VirtualMachineException {

        eventHandler.onExecuteStarted();

        //the accumulator register
        int registerA = 0;
        //the code pointer register
        int registerC = 0;
        //the data pointer register
        int registerD = 0;

        for (;;) {
            if ( memory[registerC] < 33 || memory[registerC] > 126 ) {
                eventHandler.onInstructionSkipped(registerC, memory[registerC]);
                continue;
            }

            final int xlat1Index = (memory[registerC] - 33 + registerC) % 94;
            final int opCode = Utils.xlat1.charAt(xlat1Index);
            eventHandler.onInstructionExecute(registerA, registerC, memory[registerC], registerD, memory[registerD], opCode);

            switch (opCode) {
                case 'j':
                    registerD = memory[registerD];
                    break;
                case 'i':
                    registerC = memory[registerD];
                    break;
                case '*':
                    memory[registerD] = Utils.rotr(memory[registerD]);
                    registerA = memory[registerD];
                    break;
                case 'p':
                    memory[registerD] = Utils.crz(registerA, memory[registerD]);
                    registerA = memory[registerD];
                    break;
                case '<':
                    try {
                        out.write(registerA & 0xFF);
                        out.flush();
                    } catch (IOException ioe) {
                        throw new VirtualMachineException(ioe);
                    }
                    break;
                case '/':
                    try {
                        int input = in.read();
                        if (input == '\n') {
                            input = 10;
                        } else if (input == -1) {
                            input = MAX_VALUE;
                        }
                        registerA = input;
                    } catch (IOException ioe) {
                        throw new VirtualMachineException(ioe);
                    }
                    break;
                case 'v':
                    eventHandler.onExecuteFinished();
                    return;
                default:
                    break;
            }
            memory[registerC] = Utils.xlat2.charAt(memory[registerC] - 33);
            if (registerC == MAX_VALUE) {
                registerC = 0;
            } else {
                registerC++;
            }
            if (registerD == MAX_VALUE) {
                registerD = 0;
            } else {
                registerD++;
            }
        }
    }
}
