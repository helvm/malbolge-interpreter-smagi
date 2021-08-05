package com.msagi.malbolge;

/**
 * Command line tool to execute Malbolge binary programs.
 * @author msagi
 */
public class Main {

    /**
     * Start the Malbolge binary program execution.
     * @param args The array of command line arguments.
     */
    public static void main(final String[] args) {

        if (args.length != 1) {
            System.out.println("Usage java MalbolgeVM <MalbolgeProgram.mal>");
            System.exit(-1);
        }

        try {
            final VirtualMachine virtualMachine = new VirtualMachine(System.in, System.out, VirtualMachine.FLAG_WITH_DEBUG);
            virtualMachine.load(args[0]);
            virtualMachine.execute();
        } catch (VirtualMachineException vme) {
            vme.printStackTrace();
            System.exit(-2);
        }
    }
}
