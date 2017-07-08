package Client;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.ClassicInnerBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.GradientWaveGradientPainter;
import org.jvnet.substance.skin.EmeraldDuskSkin;
import org.jvnet.substance.theme.SubstanceTerracottaTheme;
import org.jvnet.substance.title.MatteHeaderPainter;
import org.jvnet.substance.watermark.SubstanceLatchWatermark;

import DB.UserDB;

public class Login {
	private JFrame frame;
	private JPanel imagePanel;
	private ImageIcon background;
	private JTextField userName; // �û���
	private JPasswordField password; // ����
	private JLabel lableBg; // ��ǩ
	private JLabel lableUser;
	private JLabel lablePwd;
	private JButton btnLogin; // ��ť
	private JButton btnRegister;
	private JLabel Owner; // ��ǩ
	private JLabel Owner2; // ��ǩ
	private JLabel Owner3;// ��ǩ

	public static void main(String[] args) {
		new Login();
	}

	public Login() {
		try {
			// �������
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			// ��������
			SubstanceLookAndFeel
					.setCurrentTheme(new SubstanceTerracottaTheme());
			SubstanceLookAndFeel.setSkin(new EmeraldDuskSkin());
			// SubstanceLookAndFeel.setSkin(new );
			// ���ð�ť���
			SubstanceLookAndFeel
					.setCurrentButtonShaper(new ClassicButtonShaper());
			// ����ˮӡ
			SubstanceLookAndFeel
					.setCurrentWatermark(new SubstanceLatchWatermark());
			// ���ñ߿�
			SubstanceLookAndFeel
					.setCurrentBorderPainter(new ClassicInnerBorderPainter());
			// ���ý�����Ⱦ
			SubstanceLookAndFeel
					.setCurrentGradientPainter(new GradientWaveGradientPainter());
			// ���ñ���
			SubstanceLookAndFeel
					.setCurrentTitlePainter(new MatteHeaderPainter());
		} catch (Exception e) {
			System.err.println("Something went wrong!");
		}

		frame = new JFrame("Welcome Hello");
		// ���ô���ı���ͼ��
		Image touxiang = new ImageIcon("image/icon.png").getImage();
		frame.setIconImage(touxiang);
		// ����ͼƬ
		background = new ImageIcon("image/bg.jpg");
		JLabel label = new JLabel(background);// �ѱ���ͼƬ��ʾ�ڱ�ǩ��
		label.setBounds(0, 0, background.getIconWidth(),
				background.getIconHeight());// �ѱ�ǩ�Ĵ�Сλ������ΪͼƬ�պ�����������
		// �����ݴ���ת��ΪJPanel�������÷���setOpaque()��ʹ���ݴ���͸��
		imagePanel = (JPanel) frame.getContentPane();
		imagePanel.setOpaque(false);
		imagePanel.setLayout(null);// ���ò��ַ�ʽΪ���Զ�λ

		// -----------------------------------------------------------------------------
		btnLogin = new JButton("��¼");
		btnLogin.setBounds(400, 220, 100, 50);
		btnLogin.setFont(new Font("����", Font.BOLD, 20));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = userName.getText();
				String pwd = String.valueOf(password.getPassword());
				// �������ݿ�ƥ��
				UserDB c = new UserDB(name, pwd);
				if (c.selectsql() == true) {
					frame.setVisible(false);
					new Client(name);
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "�û��������벻ƥ�䣡",
							"����", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnRegister = new JButton("ע��");
		btnRegister.setBounds(250, 220, 100, 50);
		btnRegister.setFont(new Font("����", Font.BOLD, 20));
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Register();
			}
		});

		lableBg = new JLabel();
		lableBg.setIcon(new ImageIcon(touxiang));
		lableBg.setBounds(25, 20, 200, 200);
		// �û������¼�����
		userName = new JTextField("10001");
		userName.setBounds(250, 50, 250, 40);
		userName.setFont(new Font("����", Font.BOLD, 20));
		// ��¼������Աߵ�����
		lableUser = new JLabel("�˺�");
		lableUser.setBounds(200, 50, 50, 40);
		lableUser.setFont(new Font("����", Font.BOLD, 20));
		// ���������
		password = new JPasswordField("11111");
		password.setBounds(250, 150, 250, 40);
		password.setFont(new Font("����", Font.BOLD, 20));
		// ����������Աߵ�����
		lablePwd = new JLabel("����");
		lablePwd.setBounds(200, 150, 50, 40);
		lablePwd.setFont(new Font("����", Font.BOLD, 20));

		Owner = new JLabel();
		Owner.setText("������� 5 ��");
		Owner.setBounds(50, 250, 200, 50);
		Owner2 = new JLabel();
		Owner2.setText("20142203788 ��    ��");
		Owner2.setBounds(50, 270, 200, 50);
		Owner3 = new JLabel();
		Owner3.setText("20142203727 ������");
		Owner3.setBounds(50, 290, 200, 50);

		imagePanel.add(btnLogin);
		imagePanel.add(btnRegister);
		imagePanel.add(lableBg);
		imagePanel.add(userName);
		imagePanel.add(password);
		imagePanel.add(lableUser);
		imagePanel.add(lablePwd);
		imagePanel.add(Owner);
		imagePanel.add(Owner2);
		imagePanel.add(Owner3);
		
		// -----------------------------------------------------------------------------
		frame.getLayeredPane().setLayout(null);
		// �ѱ���ͼƬ��ӵ��ֲ㴰�����ײ���Ϊ����
		frame.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(background.getIconWidth(), background.getIconHeight());
		frame.setLocationRelativeTo(null);// ������ʾ
		// frame.setAlwaysOnTop(true);//������ʾ
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
}