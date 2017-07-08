package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * 
 * @author GUOFENG --服务器线程
 * 
 */
class ServerThread extends Thread {
	/*
	 * 1.用InputStreamReader（类是从字节流到字符流的桥梁）缓存到BufferedReader缓冲区 为了达到最高效率，可考虑在
	 * BufferedReader内包装InputStreamReader; 2.PrintWriter向文本输出流打印对象的格式化表示形式,此类实现在
	 * PrintStream中的所有print方法;3.PrintWriter(OutputStream out, boolean autoFlush)
	 * 通过现有的 OutputStream 创建新的 PrintWriter。
	 */
	private ServerSocket serverSocket;
	private int max;

	public ServerThread(ServerSocket serverSocket, int max) {
		this.serverSocket = serverSocket;
		this.max = max;
	}

	public void run() {
		// 循环等待客户端的链接
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				// 超过人数处理
				if (ServerUI.Clients.size() >= max) {
					BufferedReader read = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					PrintWriter write = new PrintWriter(
							socket.getOutputStream());

					// 接收客户端的用户信息
					String userInfo = read.readLine();
					StringTokenizer st = new StringTokenizer(userInfo, "@");
					User user = new User(st.nextToken(), st.nextToken());
					// 反馈连接失败信息
					write.println("MAX@亲爱的" + user.getName() + user.getIp()
							+ ",服务器在线人数已达上限，请稍后连接！");
					write.flush();
					// 释放资源
					read.close();
					write.close();
					socket.close();
					continue;
				}
				// 没超人数
				// 创建一个为客户端服务的线程
				ClientThread client = new ClientThread(socket);
				client.start();
				ServerUI.Clients.add(client);// 加入服务客户端的线程的数组
				ServerUI.userSum.setText(ServerUI.Clients.size() + " 个在线用户");
				ServerUI.listModel.addElement(client.getUser().getName());// 更新在线列表

				ServerUI.showMsg.append(client.getUser().getName()
						+ client.getUser().getIp() + " [上线]\r\n");
			} catch (IOException e) {
				System.out.println("ServerThread Run()发生异常:" + e.getMessage());
			}
		}
	}
}