package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * 
 * @author GUOFENG --为单个客户端服务的线程
 * 
 */

class ClientThread extends Thread {
	private User user;
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;

	public User getUser() {
		return user;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	// 服务客户端的线程的构造方法
	public ClientThread(Socket socket) {
		try {
			this.socket = socket;
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			// 接收客户端的用户信息
			String info = reader.readLine();
			StringTokenizer st = new StringTokenizer(info, "@");
			user = new User(st.nextToken(), st.nextToken());
			// 反馈连接成功信息
			// writer.println(user.getName() + user.getIp() + "与服务器连接成功!");
			// writer.flush();
			// 反馈当前在线用户信息
			if (ServerUI.Clients.size() > 0) {
				String temp = "";
				for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
					temp += (ServerUI.Clients.get(i).getUser().getName() + "/" + ServerUI.Clients
							.get(i).getUser().getIp())
							+ "@";
				}
				writer.println("USERLIST@" + ServerUI.Clients.size() + "@"
						+ temp);
				writer.flush();
			}
			// 向所有在线用户发送该用户上线命令
			for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
				ServerUI.Clients.get(i).getWriter()
						.println("ADD@" + user.getName() + user.getIp());
				ServerUI.Clients.get(i).getWriter().flush();
			}
		} catch (IOException e) {
			System.out.println("ClientThread ClientThread()发生异常:"
					+ e.getMessage());
		}
	}

	public void run() {
		String message = null;
		while (true) {// 不断接收客户端的消息，进行处理。
			try {
				message = reader.readLine();// 接收客户端消息
				// 客户端发来带CLOSE字符串的下线命令
				if (message.equals("CLOSE")) {
					ServerUI.showMsg.append(this.getUser().getName()
							+ this.getUser().getIp() + "[下线]\r\n");
					// 释放资源
					reader.close();
					writer.close();
					socket.close();

					// 删除此用户列表项
					ServerUI.listModel.removeElement(user.getName());

					// 向所有在线用户发送该用户的下线信号
					for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
						ServerUI.Clients.get(i).getWriter()
								.println("DELETE@" + user.getName());
						ServerUI.Clients.get(i).getWriter().flush();
					}

					// 删除此条客户端服务线程
					for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
						if (ServerUI.Clients.get(i).getUser() == user) {
							ClientThread temp = ServerUI.Clients.get(i);
							ServerUI.Clients.remove(i);// 删除此用户的服务线程
							temp.stop();// 停止这条服务线程
							return;
						}
					}
				} else {
					// 如果不关闭，就转发消息
					transmitMsg(message);
				}
			} catch (IOException e) {
				System.out.println("ClientThread Run()发生异常:" + e.getMessage());
				break;
			}
		}
	}

	// 转发消息
	public void transmitMsg(String message) {
		String[] s = message.split("@");
		String source = s[0];
		String owner = s[1];
		String content = s[2];
		String u = s[3];
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i]);
		}
		message = source + "说: " + content;
		ServerUI.showMsg.append(message + "\r\n");
		if (owner.equals("ALL")) {// 群聊
			for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
				ServerUI.Clients.get(i).getWriter().println(message + "\t[群发]");
				ServerUI.Clients.get(i).getWriter().flush();
			}
		} else if (owner.equals("ONE")) {// 私聊
			for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
				String user = ServerUI.Clients.get(i).getUser().getName();
				if (user.equals(u)) {
					ServerUI.Clients.get(i).getWriter()
							.println(message + "\t[私聊]");
					ServerUI.Clients.get(i).getWriter().flush();
				}
			}
		}
	}
}