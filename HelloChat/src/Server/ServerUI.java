package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author GUOFENG --������UI������¼�������
 * 
 */

public class ServerUI {
	private JFrame frame;
	static Label userSum;
	static JTextArea showMsg;// ��ʾ��Ϣ
	private JTextField txt_msg;// д��Ϣ
	private JTextField txt_max;// �������
	private JTextField txt_port;// �˿ں�
	private JButton btn_start;// ����
	private JButton btn_stop;// ֹͣ
	private JButton btn_send;// ����

	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightPanel;
	private JScrollPane leftPanel;
	private JSplitPane centerSplit;

	static DefaultListModel listModel;
	private JList userList;// �û��б�
	private ServerSocket serverSocket;
	private ServerThread serverThread;// �������߳�
	static ArrayList<ClientThread> Clients;// Ϊ�ͻ��˷�����߳�����
	private boolean ServerRuning = false;// ������������־λ
	private int Port;
	private int MaxUser;

	// ������
	public static void main(String[] args) {
		new ServerUI();
	}

	// ���췽��
	public ServerUI() {
		frame = new JFrame("Hello Server");
		frame.setIconImage(new ImageIcon("image/icon.png").getImage());// ����JFrame��ͼ��
		showMsg = new JTextArea();
		showMsg.setEditable(false);// ��ʾ��Ϣ�򲻿ɱ༭
		showMsg.setForeground(Color.red);
		userSum = new Label("0 �������û�");
		txt_max = new JTextField("10");// ��ʼ������
		txt_port = new JTextField("6666");// ��ʼ���˿�

		txt_msg = new JTextField();
		txt_msg.setEnabled(false);
		btn_send = new JButton("������Ϣ");
		btn_send.setEnabled(false);
		btn_start = new JButton("����������");
		btn_stop = new JButton("�رշ�����");
		btn_stop.setEnabled(false);
		listModel = new DefaultListModel();
		userList = new JList(listModel);
		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 6));
		northPanel.setBorder(new TitledBorder("������Ϣ"));
		northPanel.add(userSum);
		northPanel.add(txt_max);
		northPanel.add(new JLabel("���û�����"));
		northPanel.add(txt_port);
		northPanel.add(new JLabel("�˿�"));
		northPanel.add(btn_start);
		northPanel.add(btn_stop);
		leftPanel = new JScrollPane(userList);
		leftPanel.setBorder(new TitledBorder("�����û�"));
		rightPanel = new JScrollPane(showMsg);
		rightPanel.setBorder(new TitledBorder("��ʾ�ͻ�����Ϣ"));
		// API:JSplitPane ���ڷָ�������ֻ��������Component;
		// HORIZONTAL_SPLIT ˮƽ�ָ��ʾ Component ��x��ָ
		// setDividerLocation(int location) ���÷ָ�����λ�á�
		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
				rightPanel);
		centerSplit.setDividerLocation(150);
		southPanel = new JPanel(new BorderLayout());// ˮƽ��Ȼ����
		southPanel.setBorder(new TitledBorder("����ϵͳ��Ϣ"));
		southPanel.add(txt_msg, "Center");
		southPanel.add(btn_send, "East");
		frame.setVisible(true);
		frame.setSize(800, 500);
		frame.setLayout(new BorderLayout());
		frame.add(northPanel, "North");
		frame.add(centerSplit, "Center");
		frame.add(southPanel, "South");
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2,
				(screen_height - frame.getHeight()) / 2);

		// -----------------------------------------------------------------------

		// �رմ���ʱ�¼�
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (ServerRuning) {// ���server��������
					ServerStop();// �رշ�����
					JOptionPane.showMessageDialog(frame, "�������ɹ��ر�!");
				}
				System.exit(0);// �˳�����
			}
		});

		// �ı��򰴻س���ʱ�¼�
		txt_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SendServerMsg();// ����
			}
		});

		// �������Ͱ�ťʱ�¼�
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SendServerMsg();// ����
			}
		});

		// ����������������ťʱ�¼�
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ServerStart();// ��������������
					JOptionPane.showMessageDialog(frame, "�������ɹ�����! \n�û����� "
							+ MaxUser + "\n�˿� " + Port);
					btn_start.setEnabled(false);
					btn_stop.setEnabled(true);
					txt_max.setEnabled(false);
					txt_port.setEnabled(false);
					txt_msg.setEnabled(true);
					btn_send.setEnabled(true);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame,
							"�����������쳣" + exc.getMessage(), "����",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// ����ֹͣ��������ťʱ�¼�
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// �رշ���������
					ServerStop();
					btn_start.setEnabled(true);
					txt_max.setEnabled(true);
					txt_port.setEnabled(true);
					btn_stop.setEnabled(false);
					txt_msg.setEnabled(false);
					btn_send.setEnabled(false);
					JOptionPane.showMessageDialog(frame, "�������ɹ�ֹͣ��");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame,
							"������ֹͣ�쳣" + exc.getMessage(), "����",
							JOptionPane.ERROR_MESSAGE);
				}
				showMsg.setText(null);
			}
		});
	}

	// ����������
	public void ServerStart() throws java.net.BindException {
		// API:java.net.BindException
		// ��ͼ���׽��ְ󶨵����ص�ַ�Ͷ˿�ʱ�������������£��׳����쳣��
		// ��Щ����ͨ�������ڶ˿� ����ʹ���� �� �޷�����������ı��ص�ַ ʱ��
		MaxUser = Integer.parseInt(txt_max.getText());
		Port = Integer.parseInt(txt_port.getText());
		try {
			Clients = new ArrayList<ClientThread>();
			serverSocket = new ServerSocket(Port);
			// ��ʼ���������������߳�
			serverThread = new ServerThread(serverSocket, MaxUser);
			serverThread.start();
			ServerRuning = true;
		} catch (BindException e) {
			ServerRuning = false;
			throw new BindException("�˿ں��ѱ�ռ�ã�������˿ں�" + e.getMessage());
		} catch (Exception e) {
			ServerRuning = false;
			throw new BindException("ServerStart()�����쳣:" + e.getMessage());
		}
	}

	// �رշ�����
	public void ServerStop() {
		try {
			if (serverThread != null) {
				serverThread.stop();// ֹͣ�������߳�
				for (int i = Clients.size() - 1; i >= 0; i--) {
					// �������û����� �������ر��ź�
					Clients.get(i).getWriter().println("SERVERClOSE");
					Clients.get(i).getWriter().flush();
					// ֹͣ����Ϊ�ͻ��˷�����̲߳����ͷ���Դ
					Clients.get(i).stop();
					Clients.get(i).reader.close();
					Clients.get(i).writer.close();
					Clients.get(i).socket.close();
					// �Ƴ�����Ϊ�ͻ��˷�����߳�
					Clients.remove(i);
				}
				listModel.removeAllElements();// ����û��б�
			}
			serverSocket.close();// �رշ�����������
			ServerRuning = false;
		} catch (IOException e) {
			System.out.println(" ServerStop()�����쳣:" + e.getMessage());
			ServerRuning = true;
		}
	}

	// �������Ϣ��Ⱥ��
	public void SendServerMsg() {
		if (Clients.size() == 0) {
			JOptionPane.showMessageDialog(frame, "û���û����ߣ�", "��ʾ",
					JOptionPane.CANCEL_OPTION);
			return;
		}
		// ��ȡ������ı�, trim() �����ַ����ĸ���������ǰ���հ׺�β���հס�
		String message = txt_msg.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "��Ϣ����Ϊ��!", "��ʾ",
					JOptionPane.CANCEL_OPTION);
			return;
		}
		// Ⱥ����������Ϣ
		for (int i = Clients.size() - 1; i >= 0; i--) {
			Clients.get(i).getWriter().println("��ϵͳ��Ϣ��" + message + "\r\n");
			Clients.get(i).getWriter().flush();
		}
		showMsg.append("��ϵͳ��Ϣ��" + txt_msg.getText() + "\r\n");
		txt_msg.setText(null);
	}
}
