package com.msagi.malbolge;

/**
 * Utility class for the Malbolge Virtual Machine.
 * @author msagi
 */
public class Utils {

    /**
     * The list of virtual machine instruction codes.
     */
    public static final int[] VM_INSTRUCTIONS = {'j', 'i', '*', 'p', '<', '/', 'v', 'o'};

    /**
     * The instruction decryption table.
     */
    public static final String xlat1 =
            "+b(29e*j1VMEKLyC})8&m#~W>qxdRp0wkrUo[D7,XTcA\"lI" +
            ".v%{gJh4G\\-=O@5`_3i<?Z';FNQuY]szf$!BS/|t:Pn6^Ha";

    /**
     * The instruction encryption table.
     */
    public static final String xlat2 =
            "5z]&gqtyfr$(we4{WP)H-Zn,[%\\3dL+Q;>U!pJS72FhOA1C" +
            "B6v^=I_0/8|jsb9m<.TVac`uY*MK'X~xDl}REokN:#?G\"i@";

    /**
     * Get if given operation code is valid as virtual machine instruction or not.
     *
     * @param opCode The operation code to test.
     * @return True if operation code is valid as virtual machine instruction, false otherwise.
     */
    public static boolean isVmInstruction(int opCode) {
        for (int instructionCode : VM_INSTRUCTIONS) {
            if (instructionCode == opCode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Execute the Malbolge crazy operation.
     *
     * @return The result of the operation.
     */
    public static int crz(final int x, final int y) {
        char i = 0;
        int j = 0;
        final int[] p9 = {1, 9, 81, 729, 6561};
        final int[][] o =
                {
                        {4, 3, 3, 1, 0, 0, 1, 0, 0},
                        {4, 3, 5, 1, 0, 2, 1, 0, 2},
                        {5, 5, 4, 2, 2, 1, 2, 2, 1},
                        {4, 3, 3, 1, 0, 0, 7, 6, 6},
                        {4, 3, 5, 1, 0, 2, 7, 6, 8},
                        {5, 5, 4, 2, 2, 1, 8, 8, 7},
                        {7, 6, 6, 7, 6, 6, 4, 3, 3},
                        {7, 6, 8, 7, 6, 8, 4, 3, 5},
                        {8, 8, 7, 8, 8, 7, 5, 5, 4},
                };
        for (j = 0; j < 5; j++) {
            i += (o[y / p9[j] % 9][x / p9[j] % 9] * p9[j]);
        }
        return i;
    }

    /**
     * Malbolge standard rotate right operation.
     * @param i The value to rotate.
     * @return The result after the rotation.
     */
    public static int rotr(final int i) {
        return i / 3 + i % 3 * 19683;
    }

    /**
     * Convert a trit to string.
     * @param i The trit value.
     * @return The string representation of the trit.
     */
    public static String tritToString(int trit) {
        final StringBuilder stringBuilder = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            stringBuilder.insert(0, (char) ('0' + (trit % 3)));
            trit /= 3;
        }
        return stringBuilder.toString();
    }

    /**
     * Convert the byte data array to hex string.
     * @param data The data to convert.
     * @return The formatted hex string.
     */
    public static String toHexString(final int[] data) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < data.length; index++) {

            stringBuilder.append(toHexString(data[index])).append(" ");
            if (index % 8 == 7) {
                stringBuilder.append("| ");
            }
            if (index % 16 == 15) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Convert integer to hex string.
     * @param i The integer to convert.
     * @return The hex string.
     */
    public static String toHexString(final int i) {
        return String.format("%4s", Integer.toHexString(i)).replace(' ', '0');
    }

    /**
     * The GNU C isspace(x) equivalent function.
     *
     * @param c The character code to check.
     * @return True if c is space.
     */
    public static boolean isSpace(final int c) {
        if (c == ' ') {
            return true;
        }
        if (c == '\f') {
            return true;
        }
        if (c == '\n') {
            return true;
        }
        if (c == '\r') {
            return true;
        }
        if (c == '\t') {
            return true;
        }
        return false;
    }
}
