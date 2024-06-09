package edu.handong.csee.jh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Settings extends JFrame {
    private JTextField nicknameField;
    private String nickname = "User";

    public Settings(Launcher launcher) {
        setTitle("Settings");
        setSize(300, 200);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1)); // 2행 1열의 그리드 레이아웃으로 설정

        JLabel nicknameLabel = new JLabel("Enter your nickname:");
        nicknameField = new JTextField();

        panel.add(nicknameLabel);
        panel.add(nicknameField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nickname = nicknameField.getText();
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
}
