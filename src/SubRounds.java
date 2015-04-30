import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class SubRounds extends JApplet implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JButton firstRound = new JButton("<<");
    public static JButton previousRound = new JButton("<");
    public static JButton goToRound = new JButton("Go To Round");
    public static JButton nextRound = new JButton(">");
    public static JButton lastRound = new JButton(">>");
    public static JButton compute = new JButton("Compute");
    private int numOfRounds = 12 + 2*Menu.l;
    private int[][][][] bitString;
    private int currentRound;

    public CreateCubes cubes;
    public JPanel navBar;

    public SubRounds() {
        JPanel p = new JPanel();
        setDefaultButtonsVisibility();
        p.add(firstRound);
        p.add(previousRound);
        p.add(goToRound);
        p.add(nextRound);
        p.add(lastRound);
        p.add(compute);
        add("Center", p);
        navBar = navigationBar();
        setLayout(new BorderLayout());
        cubes = new CreateCubes();
        add("North", navBar);
        add("Center", cubes);
        bitString = new int [numOfRounds][CreateCubes.stateHeight][CreateCubes.stateWidth][cubes.stateDepth];
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

    public void goToRound(int round) {
        if (round == numOfRounds-1) {
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
        cubes.displayState(bitString[round]);
        currentRound = round;
    };

    public void computeSubRound() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                bitString[0][x][y] = Arrays.copyOf(cubes.currentBitString[x][y], cubes.currentBitString[x][y].length);
            }
        }
        switch(Menu.choosenSubRound){
            case "Theta":
                computeTheta();
                break;
            case "Rho":
                computeRho();
                break;
            case "Pi":
                computePi();
                break;
            case "Chi":
                computeChi();
                break;
            case "Iota":
                computeIota();
                break;
        }
    };

    public void computeTheta(){
        for (int r = 1; r < numOfRounds; r++) {
            bitString[r] = SHA3.theta(bitString[r - 1]);
        }
    }

    public void computeRho(){
        for (int r = 1; r < numOfRounds; r++) {
            bitString[r] = SHA3.rho(bitString[r - 1]);
        }
    }

    public void computePi(){
        for (int r = 1; r < numOfRounds; r++) {
            bitString[r] = SHA3.pi(bitString[r - 1]);
        }
    }

    public void computeChi(){
        for (int r = 1; r < numOfRounds; r++) {
            bitString[r] = SHA3.chi(bitString[r - 1]);
        }
    }

    public void computeIota(){
        for (int r = 1; r < numOfRounds; r++) {
            bitString[r] = SHA3.iota(bitString[r - 1], r-1);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == compute) {
            computeSubRound();
            compute.setVisible(false);
            goToRound.setVisible(true);
            goToRound(1);
        } else if (e.getSource() == firstRound) {
            goToRound(0);
        } else if (e.getSource() == previousRound) {
            goToRound(currentRound - 1);
        } else if (e.getSource() == goToRound) {
            JFrame frame = new JFrame("Moving Round");
            String userInput = JOptionPane.showInputDialog(frame, "Enter sub-round to go to (From 0 (inital state) to " + numOfRounds + "):");
            if (userInput != null) {
                int round = Integer.parseInt(userInput);
                if (round >= 0 && round < numOfRounds) {
                    goToRound(round);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid input");
                }
            }
        } else if (e.getSource() == nextRound) {
            goToRound(currentRound + 1);
        } else if (e.getSource() == lastRound) {
            goToRound(numOfRounds-1);
        }
    };

}