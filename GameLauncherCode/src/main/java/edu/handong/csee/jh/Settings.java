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
        panel.setLayout(new GridLayout(2, 1));

        JLabel nicknameLabel = new JLabel("Enter your nickname:");
        nicknameField = new JTextField();

        panel.add(nicknameLabel);
        panel.add(nicknameField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nickname = nicknameField.getText();
                JOptionPane.showMessageDialog(Settings.this, "Nickname saved as: " + nickname);
                setVisible(false);
                launcher.setNickname();
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
