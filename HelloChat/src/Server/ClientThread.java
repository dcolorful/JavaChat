package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * 
 * @author GUOFENG --Ϊ�����ͻ��˷�����߳�
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

	// ����ͻ��˵��̵߳Ĺ��췽��
	public ClientThread(Socket socket) {
		try {
			this.socket = socket;
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			// ���տͻ��˵��û���Ϣ
			String info = reader.readLine();
			StringTokenizer st = new StringTokenizer(info, "@");
			user = new User(st.nextToken(), st.nextToken());
			// �������ӳɹ���Ϣ
			// writer.println(user.getName() + user.getIp() + "����������ӳɹ�!");
			// writer.flush();
			// ������ǰ�����û���Ϣ
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
			// �����������û����͸��û���������
			for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
				ServerUI.Clients.get(i).getWriter()
						.println("ADD@" + user.getName() + user.getIp());
				ServerUI.Clients.get(i).getWriter().flush();
			}
		} catch (IOException e) {
			System.out.println("ClientThread ClientThread()�����쳣:"
					+ e.getMessage());
		}
	}

	public void run() {
		String message = null;
		while (true) {// ���Ͻ��տͻ��˵���Ϣ�����д���
			try {
				message = reader.readLine();// ���տͻ�����Ϣ
				// �ͻ��˷�����CLOSE�ַ�������������
				if (message.equals("CLOSE")) {
					ServerUI.showMsg.append(this.getUser().getName()
							+ this.getUser().getIp() + "[����]\r\n");
					// �ͷ���Դ
					reader.close();
					writer.close();
					socket.close();

					// ɾ�����û��б���
					ServerUI.listModel.removeElement(user.getName());

					// �����������û����͸��û��������ź�
					for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
						ServerUI.Clients.get(i).getWriter()
								.println("DELETE@" + user.getName());
						ServerUI.Clients.get(i).getWriter().flush();
					}

					// ɾ�������ͻ��˷����߳�
					for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
						if (ServerUI.Clients.get(i).getUser() == user) {
							ClientThread temp = ServerUI.Clients.get(i);
							ServerUI.Clients.remove(i);// ɾ�����û��ķ����߳�
							temp.stop();// ֹͣ���������߳�
							return;
						}
					}
				} else {
					// ������رգ���ת����Ϣ
					transmitMsg(message);
				}
			} catch (IOException e) {
				System.out.println("ClientThread Run()�����쳣:" + e.getMessage());
				break;
			}
		}
	}

	// ת����Ϣ
	public void transmitMsg(String message) {
		String[] s = message.split("@");
		String source = s[0];
		String owner = s[1];
		String content = s[2];
		String u = s[3];
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i]);
		}
		message = source + "˵: " + content;
		ServerUI.showMsg.append(message + "\r\n");
		if (owner.equals("ALL")) {// Ⱥ��
			for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
				ServerUI.Clients.get(i).getWriter().println(message + "\t[Ⱥ��]");
				ServerUI.Clients.get(i).getWriter().flush();
			}
		} else if (owner.equals("ONE")) {// ˽��
			for (int i = ServerUI.Clients.size() - 1; i >= 0; i--) {
				String user = ServerUI.Clients.get(i).getUser().getName();
				if (user.equals(u)) {
					ServerUI.Clients.get(i).getWriter()
							.println(message + "\t[˽��]");
					ServerUI.Clients.get(i).getWriter().flush();
				}
			}
		}
	}
}