package DB;

import java.sql.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * 
 * @author GUOFENG  --��¼�������ݿ�
 * 
 */
public class UserDB {
	// ����������
	String driver = "com.mysql.jdbc.Driver";
	// URLָ��Ҫ���ʵ����ݿ���hello
	String url = "jdbc:mysql://127.0.0.1:3306/hello";
	// MySQL����
	String sqluser = "root";
	String sqlpassword = "guofeng";

	String userpwd;
	String username;
	boolean n = false;

	public UserDB(String name, String pwd) {
		username = name;
		userpwd = pwd;
	}

	public Boolean selectsql() {
		n = false;
		try {
			// ��������
			Class.forName(driver);
			// �������ݿ�
			Connection conn = DriverManager.getConnection(url, sqluser,
					sqlpassword);
			if (!conn.isClosed())
				System.out.println("�������ݿ�ɹ�!");
			// statement����ִ��SQL���
			Statement statement = conn.createStatement();
			// Ҫִ�е�SQL���
			String sql = "select passworld from hellouser where username="
					+ "'" + username + "';";
			// �����
			ResultSet rs = statement.executeQuery(sql);
			String readpwd = null;
			while (rs.next()) {
				// ѡ��passworld��������
				readpwd = rs.getString("passworld");
				// ����ʹ��ISO-8859-1�ַ����������Ϊ�ֽ����в�������洢�µ��ֽ������С�
				// Ȼ��ʹ��GB2312�ַ�������ָ�����ֽ�����
				readpwd = new String(readpwd.getBytes("ISO-8859-1"), "GB2312");
				// ������
				if (readpwd.equals(userpwd)) {
					n = true;
				}
			}
			rs.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			System.out.println("����MySQL����ʧ��!");
		} catch (SQLException e1) {
			System.out.println("1.hellosql:" + e1.getMessage());
		} catch (Exception e2) {
			System.out.println("2.hellosql:" + e2.getMessage());
		}
		return n;
	}

	public boolean addsql() {
		int count = 0;
		n = false;
		try {
			// ��������
			Class.forName(driver);
			// �������ݿ�
			Connection conn = DriverManager.getConnection(url, sqluser,
					sqlpassword);
			if (!conn.isClosed())
				System.out.println("�������ݿ�ɹ�!");

			String sql = "insert  into hellouser(username,passworld) values(?,?);";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, userpwd);
			count = ps.executeUpdate();
			if (this.selectsql() == true)
				n = true;
			else {
				JOptionPane.showMessageDialog(new JFrame(), "ע��ʧ�ܣ�", "����",
						JOptionPane.ERROR_MESSAGE);
			}
			ps.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			System.out.println("����MySQL����ʧ��!");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n;
	}

}