package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.junit.Test;

public class onLineWindow extends Thread {
	public static JFrame frame;
	private JTextArea showMsg;// ��ʾ��Ϣ
	private String name;
	public onLineWindow(String username) {
		this.name=username;
	}
	@Override
	public void run() {
		frame = new JFrame("��������");
		frame.setIconImage(new ImageIcon("image/icon.png").getImage());// ����JFrame��ͼ��
		showMsg = new JTextArea();
		showMsg.setText(" "+name+" ��������");
		showMsg.setFont(new Font("����", Font.BOLD, 25));
		showMsg.setEditable(false);// ��ʾ��Ϣ�򲻿ɱ༭
		showMsg.setForeground(Color.WHITE);

		frame.setVisible(true);
		frame.setSize(250, 80);
		frame.setLayout(new BorderLayout());
		frame.add(showMsg);

		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) ,
				(screen_height - frame.getHeight())-60);
	}

}
