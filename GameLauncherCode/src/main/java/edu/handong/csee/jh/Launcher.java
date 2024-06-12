package edu.handong.csee.jh;

import edu.handong.csee.jh.chat.ChatClient;
import edu.handong.csee.jh.chat.ChatServer;
import edu.handong.csee.jh.game.*;

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

        JButton omokButton = new JButton("Omok");
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

//        JButton omokAIButton = new JButton("Omok AI");
//        omokAIButton.setBounds(100, 200, 100, 50);
//        omokAIButton.addActionListener(this);
//        contentPane.add(omokAIButton);

        JButton settingsButton = new JButton("Setting");
        settingsButton.setBounds(300, 10, 80, 30);
        settingsButton.addActionListener(this);
        contentPane.add(settingsButton);

        JButton chatClientButton = new JButton("Chat");
        chatClientButton.setBounds(400, 10, 80, 30);
        chatClientButton.addActionListener(this);
        contentPane.add(chatClientButton);

        JButton chatServerButton = new JButton("Chat Server");
        chatServerButton.setBounds(500, 10, 100, 30);
        chatServerButton.addActionListener(this);
        contentPane.add(chatServerButton);

        setVisible(true);
    }

    public void setNickname() {
        nicknameLabel.setText("Nickname: " + settings.getNickname());
    }

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Omok":
                showOmokOptions();
                break;
            case "2048":
                JOptionPane.showMessageDialog(this, "Starting the 2048 game.");
                new Game2048(this);
                break;
            case "Tic-Tac-Toe":
                showTicTacToeOptions();
                break;
//            case "Omok AI":
//                JOptionPane.showMessageDialog(this, "Starting the Omok AI mode game.");
//                OmokAI gameAI = new OmokAI(this);
//                gameAI.setVisible(true);
//                break;
            case "Chat":
                showChatClientDialog();
                break;
            case "Setting":
                settings.setVisible(true);
                break;
            case "Chat Server":
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

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                JOptionPane.showMessageDialog(Launcher.this, "Starting Single Player game.");
                TicTacToe game = new TicTacToe(Launcher.this); // 1인 플레이 모드
                game.setVisible(true);
            }
        });

        JButton aiModeButton = new JButton("AI Mode");
        aiModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                JOptionPane.showMessageDialog(Launcher.this, "Starting AI Mode game.");
                TicTacToeAI game = new TicTacToeAI(); // AI 모드
                game.setVisible(true);
            }
        });

        JButton createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showTTTCreateRoomDialog();
            }
        });

        JButton joinRoomButton = new JButton("Join Room");
        joinRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showTTTJoinRoomDialog();
            }
        });

        dialog.add(singlePlayerButton);
        dialog.add(aiModeButton);
        dialog.add(createRoomButton);
        dialog.add(joinRoomButton);

        dialog.setVisible(true);
    }

    private void showTTTCreateRoomDialog() {
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Port Number:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter server port number", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    TicTacToeServer server = new TicTacToeServer(port);
                    server.setVisible(true);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid port number.");
            }
        }
    }

    private void showTTTJoinRoomDialog() {
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("IP Address:"));
        panel.add(ipField);
        panel.add(new JLabel("Port Number:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter server information", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(this, "Please enter a valid port number.");
            }
        }
    }

    // ----------------------------------------------------------
    private void showOmokOptions() {
        JDialog dialog = new JDialog(this, "Omok Options", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300, 200);

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                JOptionPane.showMessageDialog(Launcher.this, "Starting Single Player game.");
                Omok game = new Omok(Launcher.this); // 1인 플레이 모드
                game.setVisible(true);
            }
        });

        JButton aiModeButton = new JButton("AI Mode");
        aiModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                JOptionPane.showMessageDialog(Launcher.this, "Starting AI Mode game.");
                OmokAI game = new OmokAI(Launcher.this); // AI 모드
                game.setVisible(true);
            }
        });

        JButton createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showOmokCreateRoomDialog();
            }
        });

        JButton joinRoomButton = new JButton("Join Room");
        joinRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                showOmokJoinRoomDialog();
            }
        });

        dialog.add(singlePlayerButton);
        dialog.add(aiModeButton);
        dialog.add(createRoomButton);
        dialog.add(joinRoomButton);

        dialog.setVisible(true);
    }
    private void showOmokCreateRoomDialog() {
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Port Number:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter server port number", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    OmokServer server = new OmokServer(port);
                    server.setVisible(true);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid port number.");
            }
        }
    }

    private void showOmokJoinRoomDialog() {
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("IP Address:"));
        panel.add(ipField);
        panel.add(new JLabel("Port Number:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter server information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String ipAddress = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    OmokClient client = new OmokClient(ipAddress, port);
                    client.setVisible(true);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid port number.");
            }
        }
    }
    // ----------------------------------------------------------

    private void showChatClientDialog() {
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("IP Address:"));
        panel.add(ipField);
        panel.add(new JLabel("Port Number:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter server information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String ipAddress = ipField.getText();
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    new ChatClient(settings.getNickname(), ipAddress, port);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid port number.");
            }
        }
    }

    private void showChatServerDialog() {
        JTextField portField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Port Number:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter chat server port number\n", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int port;
            try {
                port = Integer.parseInt(portField.getText());
                new Thread(() -> {
                    startChatServer(port);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid port number.");
            }
        }
    }

    private void startChatServer(int port) {
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            ChatServer.main(new String[]{String.valueOf(port)});
            SwingUtilities.invokeLater(() -> {
                chatServerInfoLabel.setText("Chat Server IP: " + ipAddress + ", Port: " + port);
            });
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "Unable to get IP address: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Chat server startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showLauncher() {
        setVisible(true);
    }
}
