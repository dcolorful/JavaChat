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
	private MessageThread messageThread;// 负责接收消息的线程
	private Map<String, User> onLineUsers = new HashMap<String, User>();// 所有在线用户
	private boolean isConnected = false;
	private int port = 6666;
	private String ip = "127.0.0.1";
	private String name;

	// 构造方法
	public Client(String n) {
		name = n;
		frame = new JFrame(name);
		frame.setIconImage(new ImageIcon("image/icon.png").getImage());

		txt_name = new JLabel(name);
		text_show = new JTextArea();
		text_show.setEditable(false);
		text_show.setForeground(Color.WHITE);
		txt_msg = new JTextField();
		btn_send = new JButton("发送");
		comboBox = new JComboBox();
		comboBox.addItem("全部");

		listModel = new DefaultListModel();
		userList = new JList(listModel);

		northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout());
		northPanel.add(new JLabel("昵称:"));
		northPanel.add(txt_name);
		northPanel.setBorder(new TitledBorder("我的信息"));

		rightScroll = new JScrollPane(text_show);
		rightScroll.setBorder(new TitledBorder("消息"));
		leftScroll = new JScrollPane(userList);
		leftScroll.setBorder(new TitledBorder("在线用户"));

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
		ConnectServer();// 连接服务器

		// /////////////////////////////////////////////////////////////
		// txt_msg回车键时事件
		txt_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ComboBoxValue();
			}
		});

		// btn_send单击发送按钮时事件
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComboBoxValue();
			}

		});

		// 关闭窗口时事件
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// if (isConnected) {
				// ConnectClose();// 关闭连接
				// }
				if (isConnected) {
					try {
						// 断开连接
						boolean flag = ConnectClose();
						if (flag == false) {
							throw new Exception("断开连接发生异常！");
						} else {
							JOptionPane.showMessageDialog(frame, "成功断开!");
							txt_msg.setEnabled(false);
							btn_send.setEnabled(false);
						}
					} catch (Exception e4) {
						JOptionPane.showMessageDialog(frame,
								"断开连接服务器异常：" + e4.getMessage(), "错误",
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
						// 这个判断是选择只会得到一个结果，如果没有判断，会得到两个相同的值，从而获取不到所要选中的值。。
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

	private String UserValue = "全部";

	// 连接服务器
	private void ConnectServer() {
		try {
			socket = new Socket(ip, port);// 根据端口号和服务器IP建立连接
			writer = new PrintWriter(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// 发送客户端基本信息(用户名和IP地址)
			sendMessage(name + "@" + socket.getLocalAddress().toString());
			// 开启不断接收消息的线程
			messageThread = new MessageThread(reader);
			messageThread.start();
			isConnected = true;// 已经连接上了
			JOptionPane.showMessageDialog(frame, name + " 连接服务器成功!");
			frame.setVisible(true);

		} catch (Exception e) {
			isConnected = false;// 未连接上
			JOptionPane.showMessageDialog(frame, "连接服务器异常：" + e.getMessage(),
					"错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 断开连接
	public synchronized boolean ConnectClose() {
		try {
			sendMessage("CLOSE");// 发送断开连接命令给服务器
			messageThread.stop();// 停止接受消息线程
			// 释放资源
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
			JOptionPane.showMessageDialog(frame, name + " 断开连接服务器成功!");
			isConnected = true;
			return false;
		}
	}

	public void ComboBoxValue() {
		String message = txt_msg.getText();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "消息不能为空！", "错误",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (UserValue.equals("全部")) {
			sendMessage(frame.getTitle() + "@" + "ALL" + "@" + message + "@"
					+ "@not");

		} else {
			sendMessage(frame.getTitle() + "@" + "ONE" + "@" + message + "@"
					+ UserValue);
		}
		txt_msg.setText(null);
	}

	// 发送消息
	public static void sendMessage(String message) {
		writer.println(message);
		writer.flush();
	}

	// ------------------------------------------------------------------------------------
	// 不断接收消息的线程
	class MessageThread extends Thread {
		private BufferedReader reader;

		// 接收消息线程的构造方法
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
					String command = stringTokenizer.nextToken();// 信号

					if (command.equals("SERVERClOSE"))// 服务器已关闭信号
					{
						text_show.append("服务器已关闭!\r\n");
						closeCon();// 关闭连接
						return;// 结束线程
					} else if (command.equals("ADD")) {// 上线更新列表信号
						String username = "";
						String userIp = "";
						if ((username = stringTokenizer.nextToken()) != null
								&& (userIp = stringTokenizer.nextToken()) != null) {
							// 上线线程
							onLineWindow t = new onLineWindow(username);
							t.start();
							try {
								t.sleep(5000);
								onLineWindow.frame.setVisible(false);
							} catch (InterruptedException e) {
								System.out.println("上线弹窗异常" + e.getMessage());
							}
							User user = new User(username, userIp);
							onLineUsers.put(username, user);
							listModel.addElement(username);
							comboBox.addItem(username);
						}
					} else if (command.equals("DELETE")) {// 下线更新列表信号
						String username = stringTokenizer.nextToken();
						User user = (User) onLineUsers.get(username);
						onLineUsers.remove(user);
						listModel.removeElement(username);
						comboBox.removeItem(username);
					} else if (command.equals("USERLIST")) {// 加载用户列表
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
					} else if (command.equals("MAX")) {// 人数已达上限信号
						text_show.append(stringTokenizer.nextToken()
								+ stringTokenizer.nextToken() + "\r\n");
						closeCon();// 关闭连接
						JOptionPane.showMessageDialog(frame, "服务器人数已满,请稍后再试！",
								"提示", JOptionPane.CANCEL_OPTION);
						return;// 结束线程
					} else {
						SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
						String time = df.format(new java.util.Date());
						text_show.append("[" + time + "]\r\n" + message
								+ "\r\n\n");// 普通消息
						
						playWAV.Play("sounds/msg.wav");
					}
				} catch (IOException e1) {
					System.out.println("客户端线程 run() e1:" + e1.getMessage());
					break;
				} catch (Exception e2) {
					System.out.println("客户端线程 run() e2:" + e2.getMessage());
					break;
				}
			}
		}

		// 服务器停止后，客户端关闭连接。
		// synchronized用来修饰一个方法或者一个代码块的时候，能够保证在同一时刻只有一个线程执行该段代码。
		public synchronized void closeCon() throws Exception {
			listModel.removeAllElements();// 清空用户列表
			// 被动的关闭连接释放资源
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
			isConnected = false;// 修改状态为断开
		}
	}
}