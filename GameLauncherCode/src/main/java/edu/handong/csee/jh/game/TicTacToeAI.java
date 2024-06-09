package edu.handong.csee.jh.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeAI extends JFrame implements ActionListener {
    private JButton[] buttons = new JButton[9];
    private boolean playerTurn = true; // true: player, false: AI
    private int moveCount = 0;

    public TicTacToeAI() {
        setTitle("TicTacToe AI Mode");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 60));
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this);
            add(buttons[i]);
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton buttonClicked = (JButton) e.getSource();

        if (playerTurn) {
            if (buttonClicked.getText().equals("")) {
                buttonClicked.setText("X");
                playerTurn = false;
                moveCount++;
                checkWin();
                aiMove();
            }
        }
    }

    private void aiMove() {
        if (moveCount < 9) {
            int bestMove = findBestMove();
            buttons[bestMove].setText("O");
            playerTurn = true;
            moveCount++;
            checkWin();
        }
    }

    private int findBestMove() {
        int bestVal = -1000;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                int moveVal = minimax(0, false);
                buttons[i].setText("");

                if (moveVal > bestVal) {
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }
        return bestMove;
    }

    private int minimax(int depth, boolean isMax) {
        int score = evaluate();

        if (score == 10)
            return score - depth;
        if (score == -10)
            return score + depth;
        if (moveCount == 9)
            return 0;

        if (isMax) {
            int best = -1000;

            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("O");
                    best = Math.max(best, minimax(depth + 1, false));
                    buttons[i].setText("");
                }
            }
            return best;
        } else {
            int best = 1000;

            for (int i = 0; i < 9; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("X");
                    best = Math.min(best, minimax(depth + 1, true));
                    buttons[i].setText("");
                }
            }
            return best;
        }
    }

    private int evaluate() {
        for (int row = 0; row < 3; row++) {
            if (buttons[row * 3].getText().equals(buttons[row * 3 + 1].getText()) &&
                buttons[row * 3 + 1].getText().equals(buttons[row * 3 + 2].getText())) {
                if (buttons[row * 3].getText().equals("O"))
                    return 10;
                else if (buttons[row * 3].getText().equals("X"))
                    return -10;
            }
        }

        for (int col = 0; col < 3; col++) {
            if (buttons[col].getText().equals(buttons[3 + col].getText()) &&
                buttons[3 + col].getText().equals(buttons[6 + col].getText())) {
                if (buttons[col].getText().equals("O"))
                    return 10;
                else if (buttons[col].getText().equals("X"))
                    return -10;
            }
        }

        if (buttons[0].getText().equals(buttons[4].getText()) &&
            buttons[4].getText().equals(buttons[8].getText())) {
            if (buttons[0].getText().equals("O"))
                return 10;
            else if (buttons[0].getText().equals("X"))
                return -10;
        }

        if (buttons[2].getText().equals(buttons[4].getText()) &&
            buttons[4].getText().equals(buttons[6].getText())) {
            if (buttons[2].getText().equals("O"))
                return 10;
            else if (buttons[2].getText().equals("X"))
                return -10;
        }

        return 0;
    }

    private void checkWin() {
        int score = evaluate();

        if (score == 10) {
            declareWinner("AI");
        } else if (score == -10) {
            declareWinner("Player");
        } else if (moveCount == 9) {
            declareWinner("Draw");
        }
    }

    private void declareWinner(String winner) {
        String message;
        if (winner.equals("Draw")) {
            message = "It's a draw!";
        } else {
            message = winner + " wins!";
        }
        JOptionPane.showMessageDialog(this, message);
        resetBoard();
    }

    private void resetBoard() {
        for (JButton button : buttons) {
            button.setText("");
        }
        playerTurn = true;
        moveCount = 0;
    }

    public static void main(String[] args) {
        new TicTacToeAI();
    }
}
