package edu.handong.csee.jh.game.game_2048;

import edu.handong.csee.jh.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game2048 extends JFrame implements KeyListener {
    private static final int BOARD_SIZE = 4; // 보드 크기
    private JButton[][] buttons; // 버튼 배열
    private int[][] board; // 게임 보드
    private Launcher launcher; // Launcher 창의 참조
    private Random random; // 랜덤 숫자 생성기
    private Map<Integer, Color> colorMap;

    public Game2048(Launcher launcher) {
        this.launcher = launcher; // Launcher 창의 참조 저장
        setTitle("2048 게임"); // 프레임 제목 설정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400); // 프레임 크기 설정

        // 숫자에 따른 색상 매핑 초기화
        initializeColorMap();

        // 게임 보드 및 버튼 배열 초기화
        board = new int[BOARD_SIZE][BOARD_SIZE];
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        random = new Random();

        // 2048 게임 패널 생성
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE)); // 그리드 레이아웃 설정

        // 버튼 배열 초기화 및 패널에 추가
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setOpaque(true);
                buttons[i][j].setBackground(getColorForNumber(0)); // 숫자에 따른 배경색 설정
                buttons[i][j].setEnabled(false);
                panel.add(buttons[i][j]);
            }
        }

        // 게임 보드 초기화 및 버튼에 숫자 설정
        initializeBoard();



        // 패널을 프레임에 추가
        add(panel);


        // 패널에 KeyListener 추가
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        panel.addKeyListener(this);

        // 게임 종료 시 Launcher 창을 다시 보이도록 설정
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                launcher.showLauncher(); // Launcher 창을 보이게 함
            }
        });

        setVisible(true); // 화면에 프레임 출력
    }

    private void initializeColorMap() {
        colorMap = new HashMap<>();
        colorMap.put(0, Color.WHITE);
        colorMap.put(2, Color.YELLOW);
        colorMap.put(4, Color.GREEN);
        colorMap.put(8, Color.BLUE);
        colorMap.put(16, Color.ORANGE);
        colorMap.put(32, Color.CYAN);
        colorMap.put(64, Color.MAGENTA);
        colorMap.put(128, Color.PINK);
        colorMap.put(256, Color.RED);
        colorMap.put(512, Color.LIGHT_GRAY);
        colorMap.put(1024, Color.GRAY);
        colorMap.put(2048, Color.DARK_GRAY);
    }
    // 숫자에 따른 배경색 반환
    private Color getColorForNumber(int number) {
        return colorMap.getOrDefault(number, Color.BLACK); // 숫자에 맞는 색상이 없을 경우 기본 색상 반환
    }

    // 게임 보드 초기화 및 버튼에 숫자 설정
    private void initializeBoard() {
        // 보드 초기화
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }

        // 버튼에 숫자 설정
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setText(Integer.toString(board[i][j]));
            }
        }
        placeRandomNumber();
        updateButtonColors();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // 키가 눌렸을 때 호출됩니다.
        int keyCode = e.getKeyCode();
        System.out.println(keyCode);
//        placeRandomNumber();
        switch (keyCode) {
            case 37:
                if (moveLeft()) {
                    placeRandomNumber();
                }
                break;
            case 38:
                if (moveUp()) {
                    placeRandomNumber();
                }
                break;
            case 39:
                if (moveRight()) {
                    placeRandomNumber();
                }
                break;
            case 40:
                if (moveDown()) {
                    placeRandomNumber();
                }
                break;
        }
        updateButtonColors();
    }
    private void updateButtonColors() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setBackground(getColorForNumber(board[i][j]));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // 키가 눌린 후 떼어질 때 호출됩니다.
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE-1; j++) {
                if (board[i][j] == 0) {
                    for (int k = j+1; k < BOARD_SIZE; k++) {
                        if (moveBtoA(i,j,i,k)) {
                            moved = true;
                            break;
                        }
                    }
                }
                for (int k = j + 1; k < BOARD_SIZE; k++) {
                    if (board[i][k] == 0) continue;
                    if (checkIsEqualAndCombine(i, j, i, k)) {
                        moved = true;
                    }
                    break;
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = BOARD_SIZE-1; j >= 0; j--) {
                if (board[i][j] == 0) {
                    for (int k = j-1; k >= 0; k--) {
                        if (moveBtoA(i,j,i,k)) {
                            moved = true;
                            break;
                        }
                    }
                }
                if (j == 0) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (board[i][k] == 0) continue;
                    if (checkIsEqualAndCombine(i, j, i, k)) {
                        moved = true;

                    }
                    break;
                }
            }
        }
        return moved;
    }
    private boolean moveUp() {
        boolean moved = false;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[j][i] == 0) {
                    for (int k = j + 1; k < BOARD_SIZE; k++) {
                        if (moveBtoA(j,i,k,i)) {
                            moved = true;
                            break;
                        }
                    }
                }
                if (j == BOARD_SIZE -1) continue;
                for (int k = j + 1; k < BOARD_SIZE; k++) {
                    if (board[k][i] == 0) continue;
                    if (checkIsEqualAndCombine(j, i, k, i)) {
                        moved = true;

                    }
                    break;
                }
            }
        }
        return moved;
    }
    private boolean moveDown() {
        boolean moved = false;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = BOARD_SIZE-1; j >= 0; j--) {
                if (board[j][i] == 0) {
                    for (int k = j-1; k >= 0; k--) {
                        if (moveBtoA(j,i,k,i)) {
                            moved = true;
                            break;
                        }
                    }
                }
                if (j == 0) continue;
                for (int k = j - 1; k >= 0; k--) {
                    if (board[k][i] == 0) continue;
                    if (checkIsEqualAndCombine(j, i, k, i)) {
                        moved = true;
                    }
                    break;
                }
            }
        }
        return moved;
    }
    private boolean moveBtoA(int a_i, int a_j, int b_i, int b_j) {
        if (board[b_i][b_j] == 0) return false;

        board[a_i][a_j] = board[b_i][b_j];
        board[b_i][b_j] = 0;
        buttons[a_i][a_j].setText(Integer.toString(board[a_i][a_j]));
        buttons[b_i][b_j].setText(Integer.toString(board[b_i][b_j]));
        return true;
    }

    private boolean checkIsEqualAndCombine(int a_i, int a_j, int b_i, int b_j) {
        if (board[a_i][a_j] == board[b_i][b_j]) {
            board[a_i][a_j] *= 2;
            board[b_i][b_j] = 0;
            buttons[a_i][a_j].setText(Integer.toString(board[a_i][a_j]));
            buttons[b_i][b_j].setText(Integer.toString(board[b_i][b_j]));
            return true;
        }

        return false;
    }
    private void placeRandomNumber() {
        // 0인 버튼들의 좌표를 저장할 리스트
        java.util.List<Point> emptyCells = new java.util.ArrayList<>();

        // 0인 버튼들의 좌표를 찾아 리스트에 저장
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new Point(i, j));
                }
            }
        }

        // 빈 버튼이 있을 때만 2로 만듭니다.
        if (!emptyCells.isEmpty()) {
            // 빈 버튼 중 하나를 랜덤하게 선택하여 2 또는 4로 설정
            Point randomPoint = emptyCells.get(random.nextInt(emptyCells.size()));
            int value = random.nextBoolean() ? 2 : 4;
            board[randomPoint.x][randomPoint.y] = value;
            buttons[randomPoint.x][randomPoint.y].setText(Integer.toString(value));
        }
    }
}
