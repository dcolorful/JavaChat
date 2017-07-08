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
	private JTextField userName; // 用户名
	private JPasswordField password; // 密码
	private JLabel lableBg; // 标签
	private JLabel lableUser;
	private JLabel lablePwd;
	private JButton btnLogin; // 按钮
	private JButton btnRegister;
	private JLabel Owner; // 标签
	private JLabel Owner2; // 标签
	private JLabel Owner3;// 标签

	public static void main(String[] args) {
		new Login();
	}

	public Login() {
		try {
			// 设置外观
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			// 设置主题
			SubstanceLookAndFeel
					.setCurrentTheme(new SubstanceTerracottaTheme());
			SubstanceLookAndFeel.setSkin(new EmeraldDuskSkin());
			// SubstanceLookAndFeel.setSkin(new );
			// 设置按钮外观
			SubstanceLookAndFeel
					.setCurrentButtonShaper(new ClassicButtonShaper());
			// 设置水印
			SubstanceLookAndFeel
					.setCurrentWatermark(new SubstanceLatchWatermark());
			// 设置边框
			SubstanceLookAndFeel
					.setCurrentBorderPainter(new ClassicInnerBorderPainter());
			// 设置渐变渲染
			SubstanceLookAndFeel
					.setCurrentGradientPainter(new GradientWaveGradientPainter());
			// 设置标题
			SubstanceLookAndFeel
					.setCurrentTitlePainter(new MatteHeaderPainter());
		} catch (Exception e) {
			System.err.println("Something went wrong!");
		}

		frame = new JFrame("Welcome Hello");
		// 设置窗体的标题图标
		Image touxiang = new ImageIcon("image/icon.png").getImage();
		frame.setIconImage(touxiang);
		// 背景图片
		background = new ImageIcon("image/bg.jpg");
		JLabel label = new JLabel(background);// 把背景图片显示在标签里
		label.setBounds(0, 0, background.getIconWidth(),
				background.getIconHeight());// 把标签的大小位置设置为图片刚好填充整个面板
		// 把内容窗格转化为JPanel，才能用方法setOpaque()来使内容窗格透明
		imagePanel = (JPanel) frame.getContentPane();
		imagePanel.setOpaque(false);
		imagePanel.setLayout(null);// 设置布局方式为绝对定位

		// -----------------------------------------------------------------------------
		btnLogin = new JButton("登录");
		btnLogin.setBounds(400, 220, 100, 50);
		btnLogin.setFont(new Font("宋体", Font.BOLD, 20));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = userName.getText();
				String pwd = String.valueOf(password.getPassword());
				// 连接数据库匹配
				UserDB c = new UserDB(name, pwd);
				if (c.selectsql() == true) {
					frame.setVisible(false);
					new Client(name);
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "用户名与密码不匹配！",
							"错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnRegister = new JButton("注册");
		btnRegister.setBounds(250, 220, 100, 50);
		btnRegister.setFont(new Font("宋体", Font.BOLD, 20));
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Register();
			}
		});

		lableBg = new JLabel();
		lableBg.setIcon(new ImageIcon(touxiang));
		lableBg.setBounds(25, 20, 200, 200);
		// 用户号码登录输入框
		userName = new JTextField("10001");
		userName.setBounds(250, 50, 250, 40);
		userName.setFont(new Font("宋体", Font.BOLD, 20));
		// 登录输入框旁边的文字
		lableUser = new JLabel("账号");
		lableUser.setBounds(200, 50, 50, 40);
		lableUser.setFont(new Font("宋体", Font.BOLD, 20));
		// 密码输入框
		password = new JPasswordField("11111");
		password.setBounds(250, 150, 250, 40);
		password.setFont(new Font("宋体", Font.BOLD, 20));
		// 密码输入框旁边的文字
		lablePwd = new JLabel("密码");
		lablePwd.setBounds(200, 150, 50, 40);
		lablePwd.setFont(new Font("宋体", Font.BOLD, 20));

		Owner = new JLabel();
		Owner.setText("软件工程 5 班");
		Owner.setBounds(50, 250, 200, 50);
		Owner2 = new JLabel();
		Owner2.setText("20142203788 郭    峰");
		Owner2.setBounds(50, 270, 200, 50);
		Owner3 = new JLabel();
		Owner3.setText("20142203727 宋仕秋");
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
		// 把背景图片添加到分层窗格的最底层作为背景
		frame.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(background.getIconWidth(), background.getIconHeight());
		frame.setLocationRelativeTo(null);// 居中显示
		// frame.setAlwaysOnTop(true);//顶层显示
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
}