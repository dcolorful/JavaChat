package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import Server.User;

public class Client {
	private PlayWAV playWAV=new PlayWAV();

	private JFrame frame;
	private JTextArea text_show;
	private JTextField txt_msg;
	private JLabel txt_name;
	private JButton btn_send;
	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightScroll;
	private JScrollPane leftScroll;
	private JSplitPane centerSplit;
	private JComboBox comboBox;

	private DefaultListModel listModel;
	private JList userList;

	private Socket socket;
	private static PrintWriter writer;
	private static BufferedReader reader;
	private MessageThread messageThread;// ���������Ϣ���߳�
	private Map<String, User> onLineUsers = new HashMap<String, User>();// ���������û�
	private boolean isConnected = false;
	private int port = 6666;
	private String ip = "127.0.0.1";
	private String name;

	// ���췽��
	public Client(String n) {
		name = n;
		frame = new JFrame(name);
		frame.setIconImage(new ImageIcon("image/icon.png").getImage());

		txt_name = new JLabel(name);
		text_show = new JTextArea();
		text_show.setEditable(false);
		text_show.setForeground(Color.WHITE);
		txt_msg = new JTextField();
		btn_send = new JButton("����");
		comboBox = new JComboBox();
		comboBox.addItem("ȫ��");

		listModel = new DefaultListModel();
		userList = new JList(listModel);

		northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout());
		northPanel.add(new JLabel("�ǳ�:"));
		northPanel.add(txt_name);
		northPanel.setBorder(new TitledBorder("�ҵ���Ϣ"));

		rightScroll = new JScrollPane(text_show);
		rightScroll.setBorder(new TitledBorder("��Ϣ"));
		leftScroll = new JScrollPane(userList);
		leftScroll.setBorder(new TitledBorder("�����û�"));

		southPanel = new JPanel(new BorderLayout());
		southPanel.add(comboBox, "West");
		southPanel.add(txt_msg, "Center");
		southPanel.add(btn_send, "East");

		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll,
				rightScroll);
		centerSplit.setDividerLocation(200);

		frame.add(northPanel, "North");
		frame.add(centerSplit, "Center");
		frame.add(southPanel, "South");
		frame.setSize(800, 500);
		int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screen_width - frame.getWidth()) / 2,
				(screen_height - frame.getHeight()) / 2);
		frame.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ConnectServer();// ���ӷ�����

		// /////////////////////////////////////////////////////////////
		// txt_msg�س���ʱ�¼�
		txt_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ComboBoxValue();
			}
		});

		// btn_send�������Ͱ�ťʱ�¼�
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComboBoxValue();
			}

		});

		// �رմ���ʱ�¼�
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// if (isConnected) {
				// ConnectClose();// �ر�����
				// }
				if (isConnected) {
					try {
						// �Ͽ�����
						boolean flag = ConnectClose();
						if (flag == false) {
							throw new Exception("�Ͽ����ӷ����쳣��");
						} else {
							JOptionPane.showMessageDialog(frame, "�ɹ��Ͽ�!");
							txt_msg.setEnabled(false);
							btn_send.setEnabled(false);
						}
					} catch (Exception e4) {
						JOptionPane.showMessageDialog(frame,
								"�Ͽ����ӷ������쳣��" + e4.getMessage(), "����",
								JOptionPane.ERROR_MESSAGE);
					}
				} else if (!isConnected) {
					ConnectServer();
					txt_msg.setEnabled(true);
					btn_send.setEnabled(true);
				}

			}
		});

		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				try {
					if (ItemEvent.SELECTED == evt.getStateChange()) {
						// ����ж���ѡ��ֻ��õ�һ����������û���жϣ���õ�������ͬ��ֵ���Ӷ���ȡ������Ҫѡ�е�ֵ����
						String value = comboBox.getSelectedItem().toString();
						System.out.println(value);
						UserValue = value;
					}
				} catch (Exception e) {
					System.out.println("GGGFFF");
				}

			}
		});

	}

	private String UserValue = "ȫ��";

	// ���ӷ�����
	private void ConnectServer() {
		try {
			socket = new Socket(ip, port);// ���ݶ˿ںźͷ�����IP��������
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// ���Ϳͻ��˻�����Ϣ(�û�����IP��ַ)
			sendMessage(name + "@" + socket.getLocalAddress().toString());
			// �������Ͻ�����Ϣ���߳�
			messageThread = new MessageThread(reader);
			messageThread.start();
			isConnected = true;// �Ѿ���������
			JOptionPane.showMessageDialog(frame, name + " ���ӷ������ɹ�!");
			frame.setVisible(true);

		} catch (Exception e) {
			isConnected = false;// δ������
			JOptionPane.showMessageDialog(frame, "���ӷ������쳣��" + e.getMessage(),
					"����", JOptionPane.ERROR_MESSAGE);
		}
	}

	// �Ͽ�����
	public synchronized boolean ConnectClose() {
		try {
			sendMessage("CLOSE");// ���ͶϿ����������������
			messageThread.stop();// ֹͣ������Ϣ�߳�
			// �ͷ���Դ
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, name + " �Ͽ����ӷ������ɹ�!");
			isConnected = true;
			return false;
		}
	}

	public void ComboBoxValue() {
		String message = txt_msg.getText();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "��Ϣ����Ϊ�գ�", "����",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (UserValue.equals("ȫ��")) {
			sendMessage(frame.getTitle() + "@" + "ALL" + "@" + message + "@"
					+ "@not");

		} else {
			sendMessage(frame.getTitle() + "@" + "ONE" + "@" + message + "@"
					+ UserValue);
		}
		txt_msg.setText(null);
	}

	// ������Ϣ
	public static void sendMessage(String message) {
		writer.println(message);
		writer.flush();
	}

	// ------------------------------------------------------------------------------------
	// ���Ͻ�����Ϣ���߳�
	class MessageThread extends Thread {
		private BufferedReader reader;

		// ������Ϣ�̵߳Ĺ��췽��
		public MessageThread(BufferedReader reader) {
			this.reader = reader;
		}

		public void run() {
			String message = "";
			while (true) {
				try {
					message = reader.readLine();
					StringTokenizer stringTokenizer = new StringTokenizer(
							message, "/@");
					String command = stringTokenizer.nextToken();// �ź�

					if (command.equals("SERVERClOSE"))// �������ѹر��ź�
					{
						text_show.append("�������ѹر�!\r\n");
						closeCon();// �ر�����
						return;// �����߳�
					} else if (command.equals("ADD")) {// ���߸����б��ź�
						String username = "";
						String userIp = "";
						if ((username = stringTokenizer.nextToken()) != null
								&& (userIp = stringTokenizer.nextToken()) != null) {
							// �����߳�
							onLineWindow t = new onLineWindow(username);
							t.start();
							try {
								t.sleep(5000);
								onLineWindow.frame.setVisible(false);
							} catch (InterruptedException e) {
								System.out.println("���ߵ����쳣" + e.getMessage());
							}
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
							comboBox.addItem(username);
						}
					} else if (command.equals("DELETE")) {// ���߸����б��ź�
						String username = stringTokenizer.nextToken();
						User user = (User) onLineUsers.get(username);
						onLineUsers.remove(user);
						listModel.removeElement(username);
						comboBox.removeItem(username);
					} else if (command.equals("USERLIST")) {// �����û��б�
						int size = Integer
								.parseInt(stringTokenizer.nextToken());
						String username = null;
						String userIp = null;
						for (int i = 0; i < size; i++) {
							username = stringTokenizer.nextToken();
							userIp = stringTokenizer.nextToken();
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
							comboBox.addItem(username);

						}
					} else if (command.equals("MAX")) {// �����Ѵ������ź�
						text_show.append(stringTokenizer.nextToken()
								+ stringTokenizer.nextToken() + "\r\n");
						closeCon();// �ر�����
						JOptionPane.showMessageDialog(frame, "��������������,���Ժ����ԣ�",
								"��ʾ", JOptionPane.CANCEL_OPTION);
						return;// �����߳�
					} else {
						SimpleDateFormat df = new SimpleDateFormat("HH:mm");// �������ڸ�ʽ
						String time = df.format(new java.util.Date());
						text_show.append("[" + time + "]\r\n" + message
								+ "\r\n\n");// ��ͨ��Ϣ
						
						playWAV.Play("sounds/msg.wav");
					}
				} catch (IOException e1) {
					System.out.println("�ͻ����߳� run() e1:" + e1.getMessage());
					break;
				} catch (Exception e2) {
					System.out.println("�ͻ����߳� run() e2:" + e2.getMessage());
					break;
				}
			}
		}

		// ������ֹͣ�󣬿ͻ��˹ر����ӡ�
		// synchronized��������һ����������һ��������ʱ���ܹ���֤��ͬһʱ��ֻ��һ���߳�ִ�иöδ��롣
		public synchronized void closeCon() throws Exception {
			listModel.removeAllElements();// ����û��б�
			// �����Ĺر������ͷ���Դ
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;// �޸�״̬Ϊ�Ͽ�
		}
	}
}