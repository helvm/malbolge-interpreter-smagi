package com.msagi.malbolge;

public class DebuggerEventHandler implements IVirtualMachineEventHandler {

    @Override
    public void onProgramDecrypted(final int[] source) {
        System.err.println("Program loaded");
    }

    @Override
    public void onExecuteStarted() {
        System.err.println("Program execution started");
    }

    @Override
    public void onInstructionSkipped(int registerC, int registerCAddress) {
        System.err.println("Instruction skipped at address: " + registerC + ", value: " + registerCAddress);
    }

    @Override
    public void onInstructionExecute(int registerA, int registerC, int registerCAddress, int registerD, int registerDAddress, int instruction) {

        final String assembly;
        switch (instruction) {
            case 'j':
                assembly = "MOV D, [D]";
                break;
            case 'i':
                assembly = "MOV C, [D]";
                break;
            case '*':
                assembly = "ROTR [D]; MOV A, [D]";
                break;
            case 'p':
                assembly = "CRZ A, [D]; MOV A, [D]";
                break;
            case '<':
                assembly = "OUT A // \"" + (char) (registerA & 0xFF) + "\"";
                break;
            case '/':
                assembly = "IN A";
                break;
            case 'o':
                assembly = "NOP";
                break;
            case 'v':
                assembly = "EXIT";
                break;
            default:
                assembly = "???";
                break;
        }
        final String message = String.format("%-25s // op: (%s), A: %6d, C: %6d, [C]: %6d, D: %6d, [D]: %6d", assembly, (char) instruction, registerA, registerC, registerCAddress, registerD, registerDAddress);
        System.err.println(message);
    }

    @Override
    public void onExecuteFinished() {
        System.err.println("Program execution finished");
    }
}
