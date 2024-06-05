package edu.handong.csee.jh;

import edu.handong.csee.jh.game.Game2048;
import edu.handong.csee.jh.game.TicTacToe;
import edu.handong.csee.jh.game.Omok;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Launcher extends JFrame implements ActionListener {
    private JLabel nicknameLabel;
    private Settings settings;

    public Launcher() {
        setTitle("Game Launcher"); // 프레임 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 400); // 프레임 크기 설정



        settings = new Settings(this);


        Container contentPane = getContentPane(); // 프레임에서 컨텐트팬 받아오기
        contentPane.setLayout(null);

        // 닉네임 표시 라벨 생성
        nicknameLabel = new JLabel();
        nicknameLabel.setBounds(10, 10, 200, 30); // 위치와 크기 설정
        setNickname();
        contentPane.add(nicknameLabel); // 프레임에 라벨 추가

        // "오목" 버튼 생성
        JButton omokButton = new JButton("오목");
        omokButton.setBounds(100, 100, 100, 50); // 위치와 크기 설정
        omokButton.addActionListener(this); // 이벤트 리스너 등록
        contentPane.add(omokButton); // 프레임에 버튼 추가

        // "2048" 버튼 생성
        JButton game2048Button = new JButton("2048");
        game2048Button.setBounds(250, 100, 100, 50); // 위치와 크기 설정
        game2048Button.addActionListener(this); // 이벤트 리스너 등록
        contentPane.add(game2048Button); // 프레임에 버튼 추가


        // "OX" 버튼 생성
        JButton oxButton = new JButton("Tic-Tac-Toe");
        oxButton.setBounds(400, 100, 100, 50); // 위치와 크기 설정
        oxButton.addActionListener(this); // 이벤트 리스너 등록
        contentPane.add(oxButton); // 프레임에 버튼 추가

        // "설정" 버튼 생성
        JButton settingsButton = new JButton("설정");
        settingsButton.setBounds(300, 10, 80, 30); // 위치와 크기 설정
        settingsButton.addActionListener(this); // 이벤트 리스너 등록
        contentPane.add(settingsButton); // 프레임에 버튼 추가

        // "채팅" 버튼 생성
        JButton chatClientButton = new JButton("채팅");
        chatClientButton.setBounds(400, 10, 80, 30); // 위치와 크기 설정
        chatClientButton.addActionListener(this); // 이벤트 리스너 등록
        contentPane.add(chatClientButton); // 프레임에 버튼 추가


        setVisible(true); // 화면에 프레임 출력
    }

    public void setNickname() {
        nicknameLabel.setText("닉네임: " + settings.getNickname());
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
    }

    // 버튼 클릭 이벤트 처리
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand(); // 클릭된 버튼의 명령어 가져오기

        // 클릭된 버튼에 따라 각각의 동작 수행
        switch (command) {
            case "오목":
                JOptionPane.showMessageDialog(this, "오목 게임을 시작합니다.");
                setVisible(false);
                 Omok game = new Omok(this);
                 game.setVisible(true);
                break;
            case "2048":
                JOptionPane.showMessageDialog(this, "2048 게임을 시작합니다.");
                setVisible(false);
                 new Game2048(this); // 2048 게임 객체 생성
                break;
            case "채팅":
                JOptionPane.showMessageDialog(this, "채팅 클라이언트를 시작합니다.");
                SwingUtilities.invokeLater(() -> {
                    ChatClient chatClient = new ChatClient(settings.getNickname(), settings.getServerAddress());
                    chatClient.setLauncher(this);
                });
                break;
            case "설정":
                settings.setVisible(true);
                break;
            case "Tic-Tac-Toe":
                JOptionPane.showMessageDialog(this, "Tic-Tac-Toe 게임을 시작합니다.");
                setVisible(false);
                SwingUtilities.invokeLater(() -> {
                    TicTacToe ttt = new TicTacToe(this); // OX 게임 객체 생성
                    ttt.setVisible(true);
                });
                break;
            default:
                break;
        }
    }

    public void showLauncher() {
        setVisible(true);
    }
}
