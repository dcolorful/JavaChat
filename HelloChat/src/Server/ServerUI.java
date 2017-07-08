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
 * @author GUOFENG --服务器UI、点击事件及调用
 * 
 */

public class ServerUI {
	private JFrame frame;
	static Label userSum;
	static JTextArea showMsg;// 显示消息
	private JTextField txt_msg;// 写消息
	private JTextField txt_max;// 最大人数
	private JTextField txt_port;// 端口号
	private JButton btn_start;// 启动
	private JButton btn_stop;// 停止
	private JButton btn_send;// 发送

	private JPanel northPanel;
	private JPanel southPanel;
	private JScrollPane rightPanel;
	private JScrollPane leftPanel;
	private JSplitPane centerSplit;

	static DefaultListModel listModel;
	private JList userList;// 用户列表
	private ServerSocket serverSocket;
	private ServerThread serverThread;// 服务器线程
	static ArrayList<ClientThread> Clients;// 为客户端服务的线程数组
	private boolean ServerRuning = false;// 服务器启动标志位
	private int Port;
	private int MaxUser;

	// 主方法
	public static void main(String[] args) {
		new ServerUI();
	}

	// 构造方法
	public ServerUI() {
		frame = new JFrame("Hello Server");
		frame.setIconImage(new ImageIcon("image/icon.png").getImage());// 更改JFrame的图标
		showMsg = new JTextArea();
		showMsg.setEditable(false);// 显示消息框不可编辑
		showMsg.setForeground(Color.red);
		userSum = new Label("0 个在线用户");
		txt_max = new JTextField("10");// 初始化人数
		txt_port = new JTextField("6666");// 初始化端口

		txt_msg = new JTextField();
		txt_msg.setEnabled(false);
		btn_send = new JButton("发送消息");
		btn_send.setEnabled(false);
		btn_start = new JButton("启动服务器");
		btn_stop = new JButton("关闭服务器");
		btn_stop.setEnabled(false);
		listModel = new DefaultListModel();
		userList = new JList(listModel);
		northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 6));
		northPanel.setBorder(new TitledBorder("配置信息"));
		northPanel.add(userSum);
		northPanel.add(txt_max);
		northPanel.add(new JLabel("个用户上限"));
		northPanel.add(txt_port);
		northPanel.add(new JLabel("端口"));
		northPanel.add(btn_start);
		northPanel.add(btn_stop);
		leftPanel = new JScrollPane(userList);
		leftPanel.setBorder(new TitledBorder("在线用户"));
		rightPanel = new JScrollPane(showMsg);
		rightPanel.setBorder(new TitledBorder("显示客户端消息"));
		// API:JSplitPane 用于分隔两个（只能两个）Component;
		// HORIZONTAL_SPLIT 水平分割表示 Component 沿x轴分割。
		// setDividerLocation(int location) 设置分隔条的位置。
		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
				rightPanel);
		centerSplit.setDividerLocation(150);
		southPanel = new JPanel(new BorderLayout());// 水平自然排列
		southPanel.setBorder(new TitledBorder("发布系统消息"));
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

		// 关闭窗口时事件
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (ServerRuning) {// 如果server正在运行
					ServerStop();// 关闭服务器
					JOptionPane.showMessageDialog(frame, "服务器成功关闭!");
				}
				System.exit(0);// 退出程序
			}
		});

		// 文本框按回车键时事件
		txt_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SendServerMsg();// 发送
			}
		});

		// 单击发送按钮时事件
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SendServerMsg();// 发送
			}
		});

		// 单击启动服务器按钮时事件
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ServerStart();// 启动服务器方法
					JOptionPane.showMessageDialog(frame, "服务器成功启动! \n用户上限 "
							+ MaxUser + "\n端口 " + Port);
					btn_start.setEnabled(false);
					btn_stop.setEnabled(true);
					txt_max.setEnabled(false);
					txt_port.setEnabled(false);
					txt_msg.setEnabled(true);
					btn_send.setEnabled(true);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame,
							"服务器启动异常" + exc.getMessage(), "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// 单击停止服务器按钮时事件
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// 关闭服务器方法
					ServerStop();
					btn_start.setEnabled(true);
					txt_max.setEnabled(true);
					txt_port.setEnabled(true);
					btn_stop.setEnabled(false);
					txt_msg.setEnabled(false);
					btn_send.setEnabled(false);
					JOptionPane.showMessageDialog(frame, "服务器成功停止！");
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(frame,
							"服务器停止异常" + exc.getMessage(), "错误",
							JOptionPane.ERROR_MESSAGE);
				}
				showMsg.setText(null);
			}
		});
	}

	// 启动服务器
	public void ServerStart() throws java.net.BindException {
		// API:java.net.BindException
		// 试图将套接字绑定到本地地址和端口时发生错误的情况下，抛出此异常。
		// 这些错误通常发生在端口 正在使用中 或 无法分配所请求的本地地址 时。
		MaxUser = Integer.parseInt(txt_max.getText());
		Port = Integer.parseInt(txt_port.getText());
		try {
			Clients = new ArrayList<ClientThread>();
			serverSocket = new ServerSocket(Port);
			// 初始化、启动服务器线程
			serverThread = new ServerThread(serverSocket, MaxUser);
			serverThread.start();
			ServerRuning = true;
		} catch (BindException e) {
			ServerRuning = false;
			throw new BindException("端口号已被占用，请更换端口号" + e.getMessage());
		} catch (Exception e) {
			ServerRuning = false;
			throw new BindException("ServerStart()发生异常:" + e.getMessage());
		}
	}

	// 关闭服务器
	public void ServerStop() {
		try {
			if (serverThread != null) {
				serverThread.stop();// 停止服务器线程
				for (int i = Clients.size() - 1; i >= 0; i--) {
					// 给所有用户发送 服务器关闭信号
					Clients.get(i).getWriter().println("SERVERClOSE");
					Clients.get(i).getWriter().flush();
					// 停止此条为客户端服务的线程并且释放资源
					Clients.get(i).stop();
					Clients.get(i).reader.close();
					Clients.get(i).writer.close();
					Clients.get(i).socket.close();
					// 移除这条为客户端服务的线程
					Clients.remove(i);
				}
				listModel.removeAllElements();// 清空用户列表
			}
			serverSocket.close();// 关闭服务器端连接
			ServerRuning = false;
		} catch (IOException e) {
			System.out.println(" ServerStop()发生异常:" + e.getMessage());
			ServerRuning = true;
		}
	}

	// 服务端消息发群送
	public void SendServerMsg() {
		if (Clients.size() == 0) {
			JOptionPane.showMessageDialog(frame, "没有用户在线！", "提示",
					JOptionPane.CANCEL_OPTION);
			return;
		}
		// 获取输入的文本, trim() 返回字符串的副本，忽略前导空白和尾部空白。
		String message = txt_msg.getText().trim();
		if (message == null || message.equals("")) {
			JOptionPane.showMessageDialog(frame, "消息不能为空!", "提示",
					JOptionPane.CANCEL_OPTION);
			return;
		}
		// 群发服务器消息
		for (int i = Clients.size() - 1; i >= 0; i--) {
			Clients.get(i).getWriter().println("【系统消息】" + message + "\r\n");
			Clients.get(i).getWriter().flush();
		}
		showMsg.append("【系统消息】" + txt_msg.getText() + "\r\n");
		txt_msg.setText(null);
	}
}
