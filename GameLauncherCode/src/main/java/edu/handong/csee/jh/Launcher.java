package edu.handong.csee.jh;


import javax.swing.*;
import java.awt.*;

public class Launcher extends JFrame{
    public Launcher() {
        setTitle("500x300 프레임 만들기"); // 프레임 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300); // 프레임 크기 설정


        Container contentPane = getContentPane(); //프레임에서 컨텐트팬 받아오기
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("레이블 테스트 입니다");
        lblNewLabel.setBounds(182, 133, 124, 15); // 레이블 위치 설정
        contentPane.add(lblNewLabel); // 콘텐트팬에 레이블 붙이기

        setVisible(true); //화면에 프레임 출력
    }

}
