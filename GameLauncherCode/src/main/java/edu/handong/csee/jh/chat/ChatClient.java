package edu.handong.csee.jh.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClient {
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;
    private Socket socket;
    private BufferedReader in;
    private String nickname;

    public ChatClient(String nickname, String serverAddress, int serverPort) {
        this.nickname = nickname;

        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 종료 버튼을 눌렀을 때 프레임만 종료되도록 설정
        frame.setSize(400, 500);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);

        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.trim().isEmpty()) {
            out.println(nickname + ": " + message);
            inputField.setText("");
        }
    }

    private class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setVisible(boolean visible) {
        // 프레임의 가시성을 설정하는 메서드
    }
}
