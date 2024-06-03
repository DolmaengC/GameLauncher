package edu.handong.csee.jh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JFrame {
    private JTextField nicknameField;
    private JTextField serverAddressField; // 서버 주소 입력 필드
    private String nickname = "User";
    private String serverAddress = "172.18.153.99";  // 서버 IP 주소

    public Settings(Launcher launcher) {
        setTitle("Settings");
        setSize(300, 200);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1)); // 3행 1열의 그리드 레이아웃으로 설정

        JLabel nicknameLabel = new JLabel("Enter your nickname:");
        nicknameField = new JTextField();

        JLabel serverAddressLabel = new JLabel("Enter server address:"); // 서버 주소 입력 라벨 생성
        serverAddressField = new JTextField(); // 서버 주소 입력 필드 생성

        panel.add(nicknameLabel);
        panel.add(nicknameField);

        panel.add(serverAddressLabel); // 서버 주소 입력 라벨 패널에 추가
        panel.add(serverAddressField); // 서버 주소 입력 필드 패널에 추가

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nickname = nicknameField.getText();
                serverAddress = serverAddressField.getText(); // 서버 주소 필드에서 입력 받은 값을 설정
                JOptionPane.showMessageDialog(Settings.this, "Settings saved."); // 설정이 저장되었음을 알리는 메시지 표시
                setVisible(false);
                launcher.setNickname(); // 설정한 닉네임을 런처에 적용
            }
        });

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setVisible(false);
    }

    public String getNickname() {
        return nickname;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
