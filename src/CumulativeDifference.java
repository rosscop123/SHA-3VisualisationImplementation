import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class CumulativeDifference extends JApplet implements ActionListener {

	private static final long serialVersionUID = 1L;
	public static JButton firstRound = new JButton("<<");
    public static JButton previousRound = new JButton("<");
    public static JButton goToRound = new JButton("Go To Round");
    public static JButton nextRound = new JButton(">");
    public static JButton lastRound = new JButton(">>");
    public static JButton compute = new JButton("Compute");


    private int numOfRounds = 12 + 2*Menu.l;
    private int[][][][] bitString;
    private int[][][][] bitStringCumulative;
    private int currentRound;

    public CreateCubes cubes;
    public JPanel navBar;

    public CumulativeDifference() {
        navBar = navigationBar();
        setLayout(new BorderLayout());
        cubes = new CreateCubes();
        add("North", navBar);
        add("Center", cubes);
        setVisible(true);
        bitString = new int [(numOfRounds * 5) + 1][CreateCubes.stateHeight][CreateCubes.stateWidth][cubes.stateDepth];
        bitStringCumulative = new int [(numOfRounds * 5) + 1][CreateCubes.stateHeight][CreateCubes.stateWidth][cubes.stateDepth];
    };

    public void reset() {
        Container parent = this.cubes.getParent();
        parent.remove(this.cubes);
        parent.validate();
        parent.repaint();
        bitString = null;
        bitStringCumulative = null;
        setDefaultButtonsVisibility();
        cubes = new CreateCubes();
        this.add("Center", cubes);
    };

    public JPanel navigationBar() {
        JPanel p = new JPanel();
        setDefaultButtonsVisibility();
        p.add(firstRound);
        p.add(previousRound);
        p.add(goToRound);
        p.add(nextRound);
        p.add(lastRound);
        p.add(compute);
        firstRound.addActionListener(this);
        previousRound.addActionListener(this);
        goToRound.addActionListener(this);
        nextRound.addActionListener(this);
        lastRound.addActionListener(this);
        compute.addActionListener(this);
        return p;
    };

    public void setDefaultButtonsVisibility() {
        firstRound.setVisible(false);
        previousRound.setVisible(false);
        goToRound.setVisible(false);
        nextRound.setVisible(false);
        lastRound.setVisible(false);
        compute.setVisible(true);
    };

    public int maxArray(int arr[][][]) {
        int max = arr[0][0][0];
        for (int x = 0; x < arr.length; x++) {
            for (int y = 0; y < arr.length; y++) {
                for (int z = 0; z < arr.length; z++) {
                    if (max < arr[x][y][z]) {
                        max = arr[x][y][z];
                    }
                }
            }
        }
        return max;
    };

    public int minArray(int arr[][][]) {
        int min = arr[0][0][0];
        for (int x = 0; x < arr.length; x++) {
            for (int y = 0; y < arr.length; y++) {
                for (int z = 0; z < arr.length; z++) {
                    if (min > arr[x][y][z]) {
                        min = arr[x][y][z];
                    }
                }
            }
        }
        return min;
    };

    public void goToRound(int round) {
        if (round == numOfRounds * 5) {
            firstRound.setVisible(true);
            previousRound.setVisible(true);
            nextRound.setVisible(false);
            lastRound.setVisible(false);
        } else if (round == 0) {
            firstRound.setVisible(false);
            previousRound.setVisible(false);
            nextRound.setVisible(true);
            lastRound.setVisible(true);
        } else {
            firstRound.setVisible(true);
            previousRound.setVisible(true);
            nextRound.setVisible(true);
            lastRound.setVisible(true);
        }
        int min = minArray(bitStringCumulative[round]);
        int max = maxArray(bitStringCumulative[round]);
        for (int x = 0; x  < CreateCubes.stateHeight; x++) {
            for (int y = 0; y  < CreateCubes.stateWidth; y++) {
                for (int z = 0; z  < cubes.stateDepth; z++) {
                    cubes.colourStateRed( round, x, y, z, bitStringCumulative[round][x][y][z], (numOfRounds * 5) + 1, min, max);
                }
            }
        }
        currentRound = round;
    };

    public void differenceBetweenStates(int[][][] originalState, int[][][] newState, int round) {
        for (int x = 0; x < CreateCubes.stateWidth; x++) {
            for (int y = 0; y < CreateCubes.stateHeight; y++) {
                for (int z = 0; z < cubes.stateDepth; z++) {
                    bitStringCumulative[round][x][y][z] = bitStringCumulative[round - 1][x][y][z] + (originalState[x][y][z] ^ newState[x][y][z]);
                }
            }
        }
    };

    public void compute_SHA3() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                bitString[0][x][y] = Arrays.copyOf(cubes.currentBitString[x][y], cubes.currentBitString[x][y].length);
            }
        }
        for (int r = 0; r < numOfRounds; r++) {
            bitString[(r * 5) + 1] = SHA3.theta(bitString[r * 5]);
            differenceBetweenStates(bitString[r * 5], bitString[(r * 5) + 1], (r * 5) + 1);
            bitString[(r * 5) + 2] = SHA3.rho(bitString[(r * 5) + 1]);
            differenceBetweenStates(bitString[(r * 5) + 1], bitString[(r * 5) + 2], (r * 5) + 2);
            bitString[(r * 5) + 3] = SHA3.pi(bitString[(r * 5) + 2]);
            differenceBetweenStates(bitString[(r * 5) + 2], bitString[(r * 5) + 3], (r * 5) + 3);
            bitString[(r * 5) + 4] = SHA3.chi(bitString[(r * 5) + 3]);
            differenceBetweenStates(bitString[(r * 5) + 3], bitString[(r * 5) + 4], (r * 5) + 4);
            bitString[(r * 5) + 5] = SHA3.iota(bitString[(r * 5) + 4], r);
            differenceBetweenStates(bitString[(r * 5) + 4], bitString[(r * 5) + 5], (r * 5) + 5);
        }
    };

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == compute) {
            compute_SHA3();
            compute.setVisible(false);
            goToRound.setVisible(true);
            goToRound(numOfRounds * 5);
        } else if (e.getSource() == firstRound) {
            goToRound(0);
        } else if (e.getSource() == previousRound) {
            goToRound(currentRound - 1);
        } else if (e.getSource() == goToRound) {
            JFrame frame = new JFrame("Moving Round");
            String userInput = JOptionPane.showInputDialog(frame, "Enter sub-round to go to (From 0 (inital state) to " + ((numOfRounds*5)+1) + "):");
            if (userInput != null) {
                int round = Integer.parseInt(userInput);
                if (round >= 0 && round < numOfRounds*5 + 1) {
                    goToRound(round);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid input");
                }
            }
        } else if (e.getSource() == nextRound) {
            goToRound(currentRound + 1);
        } else if (e.getSource() == lastRound) {
            goToRound(5 * numOfRounds);
        }
    };

}