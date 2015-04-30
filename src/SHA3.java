import java.util.*;
//import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
//import Jama.*;

public class SHA3 {

    public static int w = (int) Math.pow((double) 2, (double) 6);

    public static void printState(int[][][] state) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        for (int y = 0; y < stateHeight; y++) {
            for (int x = 0; x < stateWidth; x++) {
                for (int z = 0; z < stateDepth; z++) {
                    System.out.print(state[x][y][z]);
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void printHash(int[][][] state) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int i, x, y, z;
        String output = "";
        String outputHex = "";
        System.out.println();
        for (y = 0; y < stateHeight; y++) {
            for (x = 0; x < stateWidth; x++) {
                for (z = 0; z < stateDepth / 8; z++) {
                    for (int bit = 7; bit >= 0; bit--) {
                        if (stateDepth * (stateHeight * y + x) + z < 256) {
                            output += state[x][y][(z * 8) + bit];
                        }
                    }
                }
            }
        }
        for (i = 0; i < output.length(); i += 4) {
            outputHex += String.format("%X", Long.parseLong(output.substring(i, i + 4), 2));
        }
        System.out.println(output);
        System.out.println(outputHex);
        System.out.println("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a");
        System.out.println("C5D2460186F7233C927E7DB2DCC703C0E500B653CA82273B7BFAD8045D85A470");
    };

    // parity Operation (THETA)
    public static int[][][] theta(int[][][] state) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int[][] parity = new int[stateWidth][stateDepth];
        int[][][] newState = new int[stateWidth][stateHeight][stateDepth];

        for (int z = 0; z  < stateDepth; z++) {
            for (int x = 0; x  < stateHeight; x++) {
                parity[x][z] = 0;
                for (int y = 0; y  < stateWidth; y++) {
                    parity[x][z] ^= state[x][y][z];
                }
            }
        }
        for (int z = 0; z  < stateDepth; z++) {
            for (int y = 0; y  < stateWidth; y++) {
                for (int x = 0; x  < stateHeight; x++) {
                    newState[x][y][z] = state[x][y][z] ^ parity[((x - 1 % stateWidth) + stateWidth ) % stateWidth][z]
                                        ^ parity[(x + 1) % stateWidth][(z - 1 + stateDepth) % stateDepth];
                }
            }
        }
        return newState;
    };

    // Bitwise Rotation (RHO)
    public static int[][][] rho(int[][][] state) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int[][] rotation_const = {
            {0, 36, 3, 41, 18},
            {1, 44, 10, 45, 2},
            {62, 6, 43, 15, 61},
            {28, 55, 25, 21, 56},
            {27, 20, 39, 8, 14}
        };
        int[][][] newState = new int[stateWidth][stateHeight][stateDepth];
        for (int x = 0; x < stateWidth; x++) {
            for (int y = 0; y < stateHeight; y++) {
                for (int z = 0; z < stateDepth; z++) {
                    newState[x][y][(z + rotation_const[x][y]) % stateDepth] = state[x][y][z];
                }
            }
        }
        return newState;
    };

    // Permute 25 words (PI)
    public static int[][][] pi(int[][][] state) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int[][][] newState = new int[stateWidth][stateHeight][stateDepth];
        for (int x = 0; x < stateWidth; x++) {
            for (int y = 0; y < stateHeight; y++) {
                for (int z = 0; z < stateDepth; z++) {
                    newState[y][(2 * x + 3 * y) % stateHeight][z] = state[x][y][z];
                }
            }
        }
        return newState;
    };

    // Bitwise Combine (CHI)
    public static int[][][] chi(int[][][] state) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int[][][] newState = new int[stateWidth][stateHeight][stateDepth];
        for (int x = 0; x < stateWidth; x++) {
            for (int y = 0; y < stateHeight; y++) {
                for (int z = 0; z < stateDepth; z++) {
                    newState[x][y][z] = state[x][y][z] ^ ((1 ^ state[(x + 1) % stateWidth][y][z]) & state[(x + 2) % stateWidth][y][z]);
                }
            }
        }
        return newState;
    };

    // Exclusive-or a round constant (IOTA)
    public static int[][][] iota(int[][][] state, int round_num) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int[][][] newState = new int[stateWidth][stateHeight][stateDepth];
        Long[] round_const = {
            0x0000000000000001L,
            0x0000000000008082L,
            0x800000000000808AL,
            0x8000000080008000L,
            0x000000000000808BL,
            0x0000000080000001L,
            0x8000000080008081L,
            0x8000000000008009L,
            0x000000000000008AL,
            0x0000000000000088L,
            0x0000000080008009L,
            0x000000008000000AL,
            0x000000008000808BL,
            0x800000000000008BL,
            0x8000000000008089L,
            0x8000000000008003L,
            0x8000000000008002L,
            0x8000000000000080L,
            0x000000000000800AL,
            0x800000008000000AL,
            0x8000000080008081L,
            0x8000000000008080L,
            0x0000000080000001L,
            0x8000000080008008L
        };
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                newState[x][y] = Arrays.copyOf(state[x][y], state[x][y].length);
            }
        }
        for (int z = 0; z < stateDepth; z++) {
            newState[0][0][z] = state[0][0][z] ^ (int) ((round_const[round_num] >> z) & 1); //Starts with least significant bit
        }
        return newState;
    };

    public static int[][][] single_round(int[][][] state, int round_num) {
        int stateHeight = state.length;
        int stateWidth = state[0].length;
        int stateDepth = state[0][0].length;
        int[][][] newState = new int[stateWidth][stateHeight][stateDepth];
        newState = theta(state);
        newState = rho(newState);
        newState = pi(newState);
        newState = chi(newState);
        newState = iota(newState, round_num);
        return newState;
    };


    public static void runSHA3(String inputString) {
        int[][][] state = new int[5][5][64];
        state[0][0][0] = 1;
        state[1][3][63] = 1;
        int rounds = 12 + 2 * 6;
        int[][][] newState = new int[5][5][64];
        newState = theta(state);
        newState = rho(newState);
        newState = pi(newState);
        newState = chi(newState);
        newState = iota(newState, 0);
        for (int i = 1; i < rounds ; i++) {
            newState = theta(newState);
            newState = rho(newState);
            newState = pi(newState);
            newState = chi(newState);
            newState = iota(newState, i);
        }
        // printState(newState);
        printHash(newState);
    };

    public static void main(String[] args) {
        String inputString = "";

        runSHA3(inputString);
    };
}