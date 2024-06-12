package edu.handong.csee.jh.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OmokClient extends JFrame {
    private static final int SIZE = 15;
    private static final int CELL_SIZE = 40;
    private static final int PANEL_SIZE = SIZE * CELL_SIZE;
    private int[][] board = new int[SIZE][SIZE]; // 0: empty, 1: black, 2: white
    private boolean blackTurn = true;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean myTurn = false;

    public OmokClient(String serverAddress, int port) {
        setTitle("Omok Game (Client)");
        setSize(PANEL_SIZE, PANEL_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(myTurn ? new Color(51, 153, 255) : Color.LIGHT_GRAY);
                drawBoard(g);
                drawStones(g);
            }
        };

        panel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!myTurn) return;

                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;

                if (x < SIZE && y < SIZE && board[x][y] == 0) {
                    board[x][y] = blackTurn ? 1 : 2;
                    if (checkWin(x, y)) {
                        String winner = blackTurn ? "Black" : "White";
                        JOptionPane.showMessageDialog(panel, winner + " wins!");
                        resetBoard();
                    }
                    blackTurn = !blackTurn;
                    myTurn = false;
                    panel.repaint();

                    out.println(x * SIZE + y);
                }
            }
        });

        add(panel);
        pack();
        setVisible(true);

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(this, in.readLine());

            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        int move = Integer.parseInt(in.readLine());
                        int x = move / SIZE;
                        int y = move % SIZE;
                        if (board[x][y] == 0) {
                            board[x][y] = blackTurn ? 1 : 2;
                            if (checkWin(x, y)) {
                                String winner = blackTurn ? "Black" : "White";
                                JOptionPane.showMessageDialog(panel, winner + " wins!");
                                resetBoard();
                            }
                            blackTurn = !blackTurn;
                            myTurn = true;
                            panel.repaint();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to the server: " + e.getMessage());
            System.exit(0);
        }
    }

    private void drawBoard(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < SIZE; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, PANEL_SIZE);
            g.drawLine(0, i * CELL_SIZE, PANEL_SIZE, i * CELL_SIZE);
        }
    }

    private void drawStones(Graphics g) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (board[x][y] == 1) {
                    g.setColor(Color.BLACK);
                    g.fillOval(x * CELL_SIZE + 5, y * CELL_SIZE + 5, 30, 30);
                } else if (board[x][y] == 2) {
                    g.setColor(Color.WHITE);
                    g.fillOval(x * CELL_SIZE + 5, y * CELL_SIZE + 5, 30, 30);
                }
            }
        }
    }

    private boolean checkWin(int x, int y) {
        int player = board[x][y];
        return checkDirection(x, y, 1, 0, player) || // Horizontal
                checkDirection(x, y, 0, 1, player) || // Vertical
                checkDirection(x, y, 1, 1, player) || // Diagonal \
                checkDirection(x, y, 1, -1, player);  // Diagonal /
    }

    private boolean checkDirection(int x, int y, int dx, int dy, int player) {
        int count = 1;
        for (int i = 1; i < 5; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && board[nx][ny] == player) {
                count++;
            } else {
                break;
            }
        }
        for (int i = 1; i < 5; i++) {
            int nx = x - i * dx;
            int ny = y - i * dy;
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && board[nx][ny] == player) {
                count++;
            } else {
                break;
            }
        }
        return count >= 5;
    }

    private void resetBoard() {
        board = new int[SIZE][SIZE];
        blackTurn = true;
        myTurn = false;
    }
}

