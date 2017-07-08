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
 * @author GUOFENG --�������߳�
 * 
 */
class ServerThread extends Thread {
	/*
	 * 1.��InputStreamReader�����Ǵ��ֽ������ַ��������������浽BufferedReader������ Ϊ�˴ﵽ���Ч�ʣ��ɿ�����
	 * BufferedReader�ڰ�װInputStreamReader; 2.PrintWriter���ı��������ӡ����ĸ�ʽ����ʾ��ʽ,����ʵ����
	 * PrintStream�е�����print����;3.PrintWriter(OutputStream out, boolean autoFlush)
	 * ͨ�����е� OutputStream �����µ� PrintWriter��
	 */
	private ServerSocket serverSocket;
	private int max;

	public ServerThread(ServerSocket serverSocket, int max) {
		this.serverSocket = serverSocket;
		this.max = max;
	}

	public void run() {
		// ѭ���ȴ��ͻ��˵�����
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				// ������������
				if (ServerUI.Clients.size() >= max) {
					BufferedReader read = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					PrintWriter write = new PrintWriter(
							socket.getOutputStream());

					// ���տͻ��˵��û���Ϣ
					String userInfo = read.readLine();
					StringTokenizer st = new StringTokenizer(userInfo, "@");
					User user = new User(st.nextToken(), st.nextToken());
					// ��������ʧ����Ϣ
					write.println("MAX@�װ���" + user.getName() + user.getIp()
							+ ",���������������Ѵ����ޣ����Ժ����ӣ�");
					write.flush();
					// �ͷ���Դ
					read.close();
					write.close();
					socket.close();
					continue;
				}
				// û������
				// ����һ��Ϊ�ͻ��˷�����߳�
				ClientThread client = new ClientThread(socket);
				client.start();
				ServerUI.Clients.add(client);// �������ͻ��˵��̵߳�����
				ServerUI.userSum.setText(ServerUI.Clients.size() + " �������û�");
				ServerUI.listModel.addElement(client.getUser().getName());// ���������б�

				ServerUI.showMsg.append(client.getUser().getName()
						+ client.getUser().getIp() + " [����]\r\n");
			} catch (IOException e) {
				System.out.println("ServerThread Run()�����쳣:" + e.getMessage());
			}
		}
	}
}