package edu.handong.csee.jh;

import edu.handong.csee.jh.chat.ChatClient;
import edu.handong.csee.jh.chat.ChatServer;
import edu.handong.csee.jh.game.Game2048;
import edu.handong.csee.jh.game.Omok;
import edu.handong.csee.jh.game.TicTacToe;
import edu.handong.csee.jh.game.TicTacToeAI;
import edu.handong.csee.jh.game.TicTacToeClient;
import edu.handong.csee.jh.game.TicTacToeServer;
import edu.handong.csee.jh.game.OmokAI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Launcher extends JFrame implements ActionListener {
    private JLabel nicknameLabel;
    private JLabel chatServerInfoLabel; // 채팅 서버 정보 라벨 추가
    private Settings settings;

    public Launcher() {
        setTitle("Game Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        settings = new Settings(this);

        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        nicknameLabel = new JLabel();
        nicknameLabel.setBounds(10, 10, 200, 30);
        setNickname();
        contentPane.add(nicknameLabel);

        chatServerInfoLabel = new JLabel(); // 채팅 서버 정보 라벨 초기화
        chatServerInfoLabel.setBounds(10, 40, 400, 30);
        contentPane.add(chatServerInfoLabel);

        JButton omokButton = new JButton("오목");
        omokButton.setBounds(100, 100, 100, 50);
        omokButton.addActionListener(this);
        contentPane.add(omokButton);

        JButton game2048Button = new JButton("2048");
        game2048Button.setBounds(250, 100, 100, 50);
        game2048Button.addActionListener(this);
        contentPane.add(game2048Button);

        JButton oxButton = new JButton("Tic-Tac-Toe");
        oxButton.setBounds(400, 100, 100, 50);
        oxButton.addActionListener(this);
        contentPane.add(oxButton);

        JButton omokAIButton = new JButton("6목 AI");
        omokAIButton.setBounds(100, 200, 100, 50);
        omokAIButton.addActionListener(this);
        contentPane.add(omokAIButton);

        JButton settingsButton = new JButton("설정");
        settingsButton.setBounds(300, 10, 80, 30);
        settingsButton.addActionListener(this);
        contentPane.add(settingsButton);

        JButton chatClientButton = new JButton("채팅");
        chatClientButton.setBounds(400, 10, 80, 30);
        chatClientButton.addActionListener(this);
        contentPane.add(chatClientButton);

        JButton chatServerButton = new JButton("채팅 서버");
        chatServerButton.setBounds(500, 10, 100, 30);
        chatServerButton.addActionListener(this);
        contentPane.add(chatServerButton);

        setVisible(true);
    }

    public void setNickname() {
        nicknameLabel.setText("닉네임: " + settings.getNickname());
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "오목":
                JOptionPane.showMessageDialog(this, "오목 게임을 시작합니다.");
                Omok game = new Omok(this);
                game.setVisible(true);
                break;
            case "2048":
                JOptionPane.showMessageDialog(this, "2048 게임을 시작합니다.");
                new Game2048(this);
                break;
            case "Tic-Tac-Toe":
                showTicTacToeOptions();
                break;
            case "6목 AI":
                JOptionPane.showMessageDialog(this, "6목 AI 모드를 시작합니다.");
                OmokAI gameAI = new OmokAI(this);
                gameAI.setVisible(true);
                break;
            case "채팅":
                showChatClientDialog();
                break;
            case "설정":
                settings.setVisible(true);
                break;
            case "채팅 서버":
                showChatServerDialog();
                break;
            default:
                break;
        }
    }

    private void showTicTacToeOptions() {
        JDialog dialog = new JDialog(this, "TicTacToe Options", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300, 200);

        JButton singlePlayerButton = new JButton("1인 플레이");
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                JOptionPane.showMessageDialog(Launcher.this, "TicTacToe 1인 플레이를 시작합니다.");
                TicTacToe game = new TicTacToe(Launcher.this); // 1인 플레이 모드
                game.setVisible(true);
            }
        });

        JButton aiModeButton = new JButton("AI 모드");
        aiModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                JOptionPane.showMessageDialog(Launcher.this, "TicTacToe AI 모드를 시작합니다.");
                TicTacToeAI game = new TicTacToeAI(); // AI 모드
                game.setVisible(true);
            }
        });

        JButton createRoomButton = new JButton("방 만들기");
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showCreateRoomDialog();
            }
        });

        JButton joinRoomButton = new JButton("방 들어가기");
        joinRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showJoinRoomDialog();
            }
        });

        dialog.add(singlePlayerButton);
        dialog.add(aiModeButton);
        dialog.add(createRoomButton);
        dialog.add(joinRoomButton);

        dialog.setVisible(true);
    }

    private void showCreateRoomDialog() {
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("포트 번호:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "서버 포트 번호 입력", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    TicTacToeServer server = new TicTacToeServer(port);
                    server.setVisible(true);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "유효한 포트 번호를 입력하세요.");
            }
        }
    }

    private void showJoinRoomDialog() {
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("IP 주소:"));
        panel.add(ipField);
        panel.add(new JLabel("포트 번호:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "서버 정보 입력", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String ipAddress = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    TicTacToeClient client = new TicTacToeClient(ipAddress, port);
                    client.setVisible(true);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "유효한 포트 번호를 입력하세요.");
            }
        }
    }

    private void showChatClientDialog() {
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("IP 주소:"));
        panel.add(ipField);
        panel.add(new JLabel("포트 번호:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "서버 정보 입력", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String ipAddress = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    new ChatClient(settings.getNickname(), ipAddress, port);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "유효한 포트 번호를 입력하세요.");
            }
        }
    }

    private void showChatServerDialog() {
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("포트 번호:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "채팅 서버 포트 번호 입력", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    startChatServer(port);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "유효한 포트 번호를 입력하세요.");
            }
        }
    }

    private void startChatServer(int port) {
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            ChatServer.main(new String[]{String.valueOf(port)});
            SwingUtilities.invokeLater(() -> {
                chatServerInfoLabel.setText("채팅 서버 IP: " + ipAddress + ", 포트: " + port);
            });
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "IP 주소를 가져올 수 없습니다: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "채팅 서버 시작 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showLauncher() {
        setVisible(true);
    }
}
