package edu.handong.csee.jh.game;

import edu.handong.csee.jh.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OmokAI extends JFrame {
    private static final int SIZE = 19;
    private static final int CELL_SIZE = 40;
    private static final int PANEL_SIZE = SIZE * CELL_SIZE;
    private static final int WIN_LENGTH = 5; // 오목은 일반적으로 5목
    private static final int MAX_DEPTH = 1; // 탐색 깊이 증가
    private int[][] board = new int[SIZE][SIZE]; // 0: empty, 1: black, 2: white
    private boolean blackTurn = true;
    private boolean isAITurn = false;
    private int moveCount = 0;
    private JLabel statusLabel;
    private Map<Long, Integer> transpositionTable;
    private long[][][] zobristTable;

    public OmokAI(Launcher launcher) {
        setTitle("Omok AI Game");
        setSize(PANEL_SIZE, PANEL_SIZE + 50);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        transpositionTable = new HashMap<>();
        initZobristTable();

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawStones(g);
            }
        };

        panel.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAITurn) return;

                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;

                if (x < SIZE && y < SIZE && board[x][y] == 0) {
                    board[x][y] = 1;
                    moveCount++;
                    panel.repaint();
                    if (checkWin(x, y)) {
                        JOptionPane.showMessageDialog(panel, "Black wins!");
                        resetBoard();
                    } else {
                        blackTurn = !blackTurn;
                        updateStatusLabel();
                        if (!blackTurn) {
                            isAITurn = true;
                            new Thread(() -> aiMove(panel)).start();
                        }
                    }
                }
            }
        });

        statusLabel = new JLabel("Black's turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        pack();

        // 게임 종료 시 Launcher 창을 다시 보이도록 설정
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                launcher.showLauncher(); // Launcher 창을 보이게 함
            }
        });
    }

    private void initZobristTable() {
        zobristTable = new long[SIZE][SIZE][3];
        Random rand = new Random();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < 3; k++) {
                    zobristTable[i][j][k] = rand.nextLong();
                }
            }
        }
    }

    private long computeZobristKey() {
        long key = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    key ^= zobristTable[i][j][board[i][j] - 1]; // board[i][j] - 1로 수정
                }
            }
        }
        return key;
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

    private void aiMove(JPanel panel) {
        SwingUtilities.invokeLater(() -> statusLabel.setText("AI is calculating..."));

        long startTime = System.currentTimeMillis();
        int[] bestMove = findBestMove();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        board[bestMove[0]][bestMove[1]] = 2;
        moveCount++;
        panel.repaint();

        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("AI calculation time: " + elapsedTime + " ms");
            if (checkWin(bestMove[0], bestMove[1])) {
                JOptionPane.showMessageDialog(panel, "White (AI) wins!");
                resetBoard();
                statusLabel.setText("Black's turn");
            } else {
                blackTurn = !blackTurn;
                isAITurn = false;
                updateStatusLabel();
            }
        });
    }

    private int[] findBestMove() {
        int bestVal = -100000;
        int[] bestMove = new int[2];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = 2;
                    moveCount++;
                    int moveVal = minimax(0, false, -100000, 100000);
                    board[i][j] = 0;
                    moveCount--;

                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(int depth, boolean isMax, int alpha, int beta) {
        long key = computeZobristKey();
        if (transpositionTable.containsKey(key)) {
            return transpositionTable.get(key);
        }

        int score = evaluate();

        if (score == 1000000)
            return score - depth;
        if (score == -1000000)
            return score + depth;
        if (moveCount == SIZE * SIZE || depth == MAX_DEPTH)
            return score;

        if (isMax) {
            int best = -100000;

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 2;
                        moveCount++;
                        best = Math.max(best, minimax(depth + 1, false, alpha, beta));
                        board[i][j] = 0;
                        moveCount--;
                        alpha = Math.max(alpha, best);

                        if (beta <= alpha) {
                            transpositionTable.put(key, best);
                            return best;
                        }
                    }
                }
            }
            transpositionTable.put(key, best);
            return best;
        } else {
            int best = 100000;

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 1;
                        moveCount++;
                        best = Math.min(best, minimax(depth + 1, true, alpha, beta));
                        board[i][j] = 0;
                        moveCount--;
                        beta = Math.min(beta, best);

                        if (beta <= alpha) {
                            transpositionTable.put(key, best);
                            return best;
                        }
                    }
                }
            }
            transpositionTable.put(key, best);
            return best;
        }
    }

    private int evaluate() {
        int score = 0;

        // 가중치 기반의 평가 함수
        score += evaluateLines(2) * 10; // AI의 돌
        score -= evaluateLines(1) * 10; // 플레이어의 돌

        return score;
    }

    private int evaluateLines(int player) {
        int score = 0;

        // 가로, 세로, 대각선(오른쪽 아래), 대각선(왼쪽 아래) 방향으로 평가
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                score += evaluateDirection(i, j, 1, 0, player); // 가로
                score += evaluateDirection(i, j, 0, 1, player); // 세로
                score += evaluateDirection(i, j, 1, 1, player); // 대각선 (오른쪽 아래)
                score += evaluateDirection(i, j, 1, -1, player); // 대각선 (왼쪽 아래)
            }
        }

        return score;
    }

    private int evaluateDirection(int row, int col, int rowDelta, int colDelta, int player) {
        int count = 0;
        int blocks = 0;
        int empty = 0;

        for (int i = 0; i < WIN_LENGTH; i++) {
            int newRow = row + i * rowDelta;
            int newCol = col + i * colDelta;

            if (newRow < 0 || newRow >= SIZE || newCol < 0 || newCol >= SIZE) {
                blocks++;
                continue;
            }
            if (board[newRow][newCol] == player) {
                count++;
            } else if (board[newRow][newCol] == 0) {
                empty++;
            } else {
                blocks++;
                break;
            }
        }

        // 점수 계산
        if (count == WIN_LENGTH) {
            return 1000000; // 승리 조건을 만족할 때 매우 높은 점수 반환
        }

        if (blocks == 2) {
            return 0; // 양쪽이 막힌 경우
        }

        if (count == 4 && empty == 1) {
            return 10000; // 열린 4
        }

        if (count == 3 && empty == 2) {
            return 1000; // 열린 3
        }

        if (count == 2 && empty == 3) {
            return 100; // 열린 2
        }

        if (count == 1 && empty == 4) {
            return 10; // 열린 1
        }

        return count; // 그 외의 경우는 연속된 돌의 개수에 비례한 점수
    }

    private boolean checkLine(int row, int col, int rowDelta, int colDelta, int player) {
        int count = 0;

        for (int i = 0; i < WIN_LENGTH; i++) {
            int newRow = row + i * rowDelta;
            int newCol = col + i * colDelta;

            if (newRow < 0 || newRow >= SIZE || newCol < 0 || newCol >= SIZE || board[newRow][newCol] != player) {
                break;
            }
            count++;
        }
        return count == WIN_LENGTH;
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
        for (int i = 1; i < WIN_LENGTH; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && board[nx][ny] == player) {
                count++;
            } else {
                break;
            }
        }
        for (int i = 1; i < WIN_LENGTH; i++) {
            int nx = x - i * dx;
            int ny = y - i * dy;
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && board[nx][ny] == player) {
                count++;
            } else {
                break;
            }
        }
        return count >= WIN_LENGTH;
    }

    private void resetBoard() {
        board = new int[SIZE][SIZE];
        blackTurn = true;
        isAITurn = false;
        moveCount = 0;
        transpositionTable.clear();
        updateStatusLabel();
        repaint();
    }

    private void updateStatusLabel() {
        if (blackTurn) {
            statusLabel.setText("Black's turn");
        } else {
            statusLabel.setText("White (AI)'s turn");
        }
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        new OmokAI(launcher);
    }
}
