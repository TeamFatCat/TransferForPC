package transfer.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultCaret;

import transfer.control.TransferManager;
import transfer.net.SendThread;
import transfer.scan.SearchEndOper;
import transfer.scan.SearchTool;

/**
 * 用户界面
 * @author EsauLu
 *
 */
public class MainFrame extends JFrame {

	//显示本机信息
	private JLabel m_icon_lable;	//图标
	private JLabel m_name_lable;
	private JTextField m_name_field;	//主机名
	private JLabel m_address_lable;
	private JTextField m_address_field;	//主机地址
	private JLabel m_setting_label;	//服务器设置
	
	//管理选项
	private JLabel m_home_lable;	//提示
	private JLabel m_config_lable;	//配置
	private JLabel m_log_lable;	//日志
	
	//使用提示
	private JLabel m_tip_img_lable;
	private JLabel m_tip_host_lable;
	
	//卡片布局
	private CardLayout m_card_layout;
	private JPanel m_card_panel;
	
	//发送文件
	private JLabel m_send_path_label;
	private JTextField m_send_path_field;
	private JLabel m_send_ip_label;
	private JComboBox<String> m_send_ip_field;
//	private JLabel m_send_port_label;
//	private JTextField m_send_port_field;
	private JButton m_send_btn;
	private JLabel m_choose_file_btn;
	private JLabel m_scan_host_btn;
	
	//文件选择对话框
	private JFileChooser m_file_chooser;
	
	//日志
	private JScrollPane m_log_scroll_pane;
	private JTextArea m_log_area;
	private JButton m_clear_log_btn;
	
	//鼠标事件适配器
	private MouseAdapter m_mouse_adapter;
	
	//设置对话框
	private SettingDialog m_setting_dialog;
	
	//监听局域网设备的线程
	private HashMap<String , String> m_host_map;
	
	private SearchEndOper m_search_end_oper;
	
	/**
	 * 上下文
	 */
	private TransferManager mContext;

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 构造函数
	 * @param context 程序上下文
	 */
	public MainFrame(TransferManager context) {
		// TODO Auto-generated constructor stub
		this.mContext=context;
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		// TODO Auto-generated method stub

		initData();
		initLayout();
		setEventListener();
		
		updateHostInfo(mContext.getHostName(),mContext.getIp());
		
		scanHost();

		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension srceen=kit.getScreenSize();
		Dimension preferredSize=new Dimension(getPreferredSize().width+50, getPreferredSize().height+50);
		this.setTitle("文件传输");
		this.setSize(preferredSize);		
		this.setLocation((srceen.width-preferredSize.width)/2,(srceen.height-preferredSize.height)/2);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				mContext.stopAP();
			}
		});
		this.setVisible(true);
		
	}
	
	/**
	 * 初始化界面布局
	 */
	private void initLayout() {
		// TODO Auto-generated method stub
		
		Color bg=new Color(27, 202, 234);
		
		//显示本机信息
		JPanel top_layout=new JPanel(new BorderLayout(0, 10));	
		
		JPanel info_layout=new JPanel(new GridBagLayout());
		JPanel table=new JPanel();
		GridBagLayout layout=new GridBagLayout();
		GridBagConstraints c=new GridBagConstraints();
		table.setLayout(layout);
		c.gridx=0;
		c.gridy=0;
		c.insets=new Insets(0, 0, 5, 0);
		layout.setConstraints(m_name_lable, c);
		table.add(m_name_lable);
		c.gridx=1;
		c.gridy=0;
		layout.setConstraints(m_name_field, c);
		table.add(m_name_field);
		c.gridx=0;
		c.gridy=1;
		c.insets=new Insets(5, 0, 0, 0);
		layout.setConstraints(m_address_lable, c);
		table.add(m_address_lable);
		c.gridx=1;
		c.gridy=1;
		layout.setConstraints(m_address_field, c);
		table.add(m_address_field);
		table.setBackground(bg);
		
		info_layout.add(table);
		info_layout.setBackground(bg);
		
		top_layout.add(info_layout);
		top_layout.add(m_icon_lable,BorderLayout.WEST);
		top_layout.add(m_setting_label,BorderLayout.EAST);
		top_layout.setBackground(bg);
		top_layout.setBorder(BorderFactory.createLineBorder(bg, 20));
		
		this.add(top_layout,BorderLayout.NORTH);
		
		//功能界面
		JPanel contain=new JPanel(new BorderLayout());
		
		JPanel card_layout=new JPanel(new GridLayout());
		card_layout.setBackground(Color.white);
		card_layout.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, bg));
		card_layout.add(m_home_lable);
		card_layout.add(m_config_lable);
		card_layout.add(m_log_lable);
		
		
		//卡片布局内容
		m_card_layout=new CardLayout();
		m_card_panel=new JPanel(m_card_layout);		

		m_card_panel.add(m_home_lable.getText(),builtCard0());	
		m_card_panel.add(m_config_lable.getText(),builtCard1());	
		m_card_panel.add(m_log_lable.getText(),builtCard2());	

		JPanel tem_jp=new JPanel(new GridLayout());
		tem_jp.setBackground(Color.white);
		tem_jp.setBorder(BorderFactory.createLineBorder(Color.white, 5));
		tem_jp.add(card_layout);
		contain.add(tem_jp,BorderLayout.NORTH);	
		contain.add(m_card_panel);
		
		this.add(contain);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		Color bg=new Color(27, 202, 234);
		Color fg=new Color(255,255,255);
		
		m_name_lable=new JLabel("名称：");
		m_name_lable.setForeground(fg);
		m_name_lable.setFont(new Font("宋体", Font.PLAIN, 18));
		
		m_address_lable=new JLabel("地址：");
		m_address_lable.setForeground(fg);
		m_address_lable.setFont(new Font("宋体", Font.PLAIN, 18));
		
		m_name_field=new JTextField("null",15);
		m_name_field.setEditable(false);
		m_name_field.setBackground(bg);
		m_name_field.setForeground(Color.white);
		m_name_field.setFont(new Font("宋体", Font.PLAIN, 18));
		
		m_address_field=new JTextField("0.0.0.0:0",15);
		m_address_field.setEditable(false);
		m_address_field.setBackground(bg);
		m_address_field.setForeground(Color.white);
		m_address_field.setFont(new Font("宋体", Font.PLAIN, 18));

		m_home_lable=new JLabel("使用提示",JLabel.CENTER);
		m_home_lable.setForeground(bg);
		m_home_lable.setFont(new Font("黑体", Font.PLAIN, 22));
		m_home_lable.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, bg));
		
		m_config_lable=new JLabel("发送文件",JLabel.CENTER);
		m_config_lable.setForeground(Color.gray);
		m_config_lable.setFont(new Font("黑体", Font.PLAIN, 22));
		m_config_lable.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.white));
		
		m_log_lable=new JLabel("传输记录",JLabel.CENTER);
		m_log_lable.setForeground(Color.gray);
		m_log_lable.setFont(new Font("黑体", Font.PLAIN, 22));
		m_log_lable.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.white));
		
		m_tip_host_lable=new JLabel();
		m_tip_host_lable.setFont(new Font("宋体", Font.PLAIN, 16));		
		m_tip_img_lable=new JLabel(new ImageIcon("resource/icon/transfer3.png"));
		
		m_send_path_label=new JLabel("文件路径：");
		m_send_path_field=new JTextField(20);
		m_send_path_field.setEditable(false);
		m_send_path_field.setBackground(Color.white);
		m_send_ip_label=new JLabel("选择设备：");
		m_send_ip_field=new JComboBox<String>();
		m_send_ip_field.setEditable(true);
		m_send_btn=new JButton("发送文件");
		try {
			int width=50;
			
			BufferedImage img1=new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
			Image tem_img1=ImageIO.read(new File("resource/icon/computer2_72px.png"));
			Graphics2D g1=(Graphics2D)img1.getGraphics();
			g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g1.setColor(bg);
			g1.fillRect(0, 0, width,width);
			g1.drawImage(tem_img1.getScaledInstance(width, width, Image.SCALE_SMOOTH), 0,0, null);
			m_icon_lable=new JLabel(new ImageIcon(img1),JLabel.CENTER);
			
			BufferedImage img2=new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
			Image tem_img2=ImageIO.read(new File("resource/icon/setting_128px.png"));
			Graphics2D g2=(Graphics2D)img2.getGraphics();
			g2.setColor(bg);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fillRect(0, 0, width,width);
			g2.drawImage(tem_img2.getScaledInstance(width-4, width-4, Image.SCALE_SMOOTH), 2,2, null);
			m_setting_label=new JLabel(new ImageIcon(img2),JLabel.CENTER);
			
			width=m_send_ip_field.getPreferredSize().height;
			
			BufferedImage img3=new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
			Image tem_img3=ImageIO.read(new File("resource/icon/folder.png"));
			Graphics2D g3=(Graphics2D)img3.getGraphics();
			g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g3.fillRect(0, 0, width,width);
			g3.drawImage(tem_img3.getScaledInstance(width, width, Image.SCALE_SMOOTH), 0,0, null);
			m_choose_file_btn=new JLabel(new ImageIcon(img3));
			m_choose_file_btn.setBorder(BorderFactory.createLineBorder(Color.white, 1));
			
			BufferedImage img4=new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
			Image tem_img4=ImageIO.read(new File("resource/icon/search3.png"));
			Graphics2D g4=(Graphics2D)img4.getGraphics();
			g4.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g4.fillRect(0, 0, width,width);
			g4.drawImage(tem_img4.getScaledInstance(width, width, Image.SCALE_SMOOTH), 0,0, null);
			m_scan_host_btn=new JLabel(new ImageIcon(img4));
			m_scan_host_btn.setBorder(BorderFactory.createLineBorder(Color.white, 1));
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}
		
		m_log_area=new JTextArea();
		m_log_area.setEditable(false);
		m_clear_log_btn=new JButton("清除");
		
		m_file_chooser=new JFileChooser();
		m_file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		m_setting_dialog=new SettingDialog(this);
		m_host_map=new HashMap<String, String>();
		m_search_end_oper=new SearchEndOper() {
			
			@Override
			public void doIPMapWork(Map<String, String> map) {
				// TODO Auto-generated method stub
				m_host_map=(HashMap<String, String>)map;
				updataScan();
				m_send_btn.setEnabled(true);
				m_send_ip_field.setEditable(true);
			}
			
			@Override
			public void doIPListWork(List<String> ip_arr) {
				// TODO Auto-generated method stub				
			}
		};
	}
	
	/**
	 * 卡片0
	 * @return
	 */
	private JPanel builtCard0(){
		
		JPanel card2=new JPanel(new BorderLayout());		
		card2.setBackground(Color.white);
		
		JPanel img_panel=new JPanel(new GridLayout());
		img_panel.setBackground(Color.white);
		img_panel.add(m_tip_img_lable);
		img_panel.setBorder(BorderFactory.createMatteBorder(30, 0, 0, 0, Color.white));
		card2.add(img_panel);
		
		JPanel tip=new JPanel(new GridLayout(2,1,10,10));
		tip.setBackground(Color.white);
		JLabel jl=new JLabel("1、将设备连接至和本机同一网络");
		jl.setFont(new Font("宋体", Font.PLAIN, 16));
		tip.add(jl);
		m_tip_host_lable.setText("<html>2、发送文件时选择设备：<font color='red'>"+mContext.getHostName()+"</font></html>");
		tip.add(m_tip_host_lable);	
		
		JPanel tem=new JPanel(new FlowLayout(FlowLayout.CENTER));
		tem.setBackground(Color.white);
		tem.add(tip);
		tem.setBorder(BorderFactory.createMatteBorder(30, 0, 30, 0, Color.white));
		
		card2.add(tem,BorderLayout.SOUTH);

		return card2;
	}

	/**
	 * 卡片1
	 * @return
	 */
	private JPanel builtCard1(){
		
		JPanel card1=new JPanel(new GridLayout());		
		Box layout=Box.createVerticalBox();
		
		JPanel jp1=new JPanel();
		GridBagLayout gbl=new GridBagLayout();
		GridBagConstraints s=new GridBagConstraints();
		s.gridx=0;
		s.gridy=0;
		s.insets=new Insets(20, 0, 10, 0);
		s.anchor=GridBagConstraints.EAST;
		gbl.setConstraints(m_send_ip_label, s);
		jp1.add(m_send_ip_label);
		s.gridx=1;
		s.gridy=0;
		s.fill=GridBagConstraints.HORIZONTAL;
		
		JPanel ip_list_panel=new JPanel(new BorderLayout());
		ip_list_panel.add(m_send_ip_field);		
		ip_list_panel.setMaximumSize(ip_list_panel.getPreferredSize());
		
		gbl.setConstraints(ip_list_panel, s);
		jp1.add(ip_list_panel);	
		s.gridx=2;
		s.gridy=0;
		s.insets=new Insets(20, 10, 10, 0);
		gbl.setConstraints(m_scan_host_btn, s);
		jp1.add(m_scan_host_btn);	
		s.gridx=0;
		s.gridy=2;
		s.insets=new Insets(10, 0, 15, 0);
		gbl.setConstraints(m_send_path_label, s);
		jp1.add(m_send_path_label);
		s.gridx=1;
		s.gridy=2;
		gbl.setConstraints(m_send_path_field, s);
		jp1.add(m_send_path_field);		
		s.gridx=2;
		s.gridy=2;
		s.insets=new Insets(10, 10, 15, 0);
		gbl.setConstraints(m_choose_file_btn, s);
		jp1.add(m_choose_file_btn);	

		JPanel tem_jp2=new JPanel(new FlowLayout(FlowLayout.CENTER,20,0));
		tem_jp2.setBackground(Color.white);
		tem_jp2.add(m_send_btn);
		s.gridx=0;
		s.gridy=3;
		s.fill=GridBagConstraints.BOTH;
		s.gridwidth=3;
		s.insets=new Insets(5, 0, 30, 0);
		gbl.setConstraints(tem_jp2, s);
		jp1.add(tem_jp2);		
		
		jp1.setBackground(Color.white);
		jp1.setLayout(gbl);
		layout.add(jp1);		
		
		card1.setBackground(Color.white);
		card1.add(layout);
		card1.setMaximumSize(card1.getPreferredSize());
		return card1;
	}
	
	/**
	 * 卡片2
	 * @return
	 */
	private JPanel builtCard2(){
		
		JPanel card1=new JPanel(new BorderLayout());		
		JPanel log_panel=new JPanel(new BorderLayout());
		JPanel clear_panel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		Border lb=BorderFactory.createLineBorder(Color.white, 5);
		
		m_log_scroll_pane=new JScrollPane(m_log_area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		log_panel.add(m_log_scroll_pane);
		log_panel.setBorder(BorderFactory.createMatteBorder(10, 10, 5, 10, Color.white));
		log_panel.setBackground(Color.white);
		clear_panel.add(m_clear_log_btn);
		clear_panel.setBorder(BorderFactory.createMatteBorder(0, 10, 10, 10, Color.white));
		clear_panel.setBackground(Color.white);
		
		card1.add(log_panel);
		card1.add(clear_panel,BorderLayout.SOUTH);
		card1.setBackground(Color.white);
		return card1;
	}
	
	private void setEventListener(){
		
		m_mouse_adapter=new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseEntered(arg0);
				JLabel jl=(JLabel)arg0.getSource();
				Color c=new Color(27, 202, 234);
				if(jl.getForeground().equals(c)){
					return;
				}
				jl.setForeground(c);
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseExited(arg0);
				JLabel jl=(JLabel)arg0.getSource();
				Color c=new Color(27, 202, 234);
				MatteBorder border=(MatteBorder)jl.getBorder();
				if(border.getMatteColor().equals(c)){
					return;
				}
				jl.setForeground(Color.gray);
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseClicked(arg0);
				JLabel jl=(JLabel)arg0.getSource();
				String text=jl.getText();
				Color c=new Color(27, 202, 234);
				if(!text.equals(m_home_lable.getText())){
					m_home_lable.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.white));
					m_home_lable.setForeground(Color.gray);
				}
				if(!text.equals(m_config_lable.getText())){
					m_config_lable.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.white));
					m_config_lable.setForeground(Color.gray);
				}
				if(!text.equals(m_log_lable.getText())){
					m_log_lable.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.white));
					m_log_lable.setForeground(Color.gray);
				}
				jl.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, c));
				jl.setForeground(c);
				m_card_layout.show(m_card_panel, text);		
			}
		};
		
		m_home_lable.addMouseListener(m_mouse_adapter);
		m_config_lable.addMouseListener(m_mouse_adapter);
		m_log_lable.addMouseListener(m_mouse_adapter);
		
		m_setting_label.addMouseListener(new MouseAdapter() {
			private Color bg=new Color(27, 202, 234);
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				m_setting_dialog.setVisible(true);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseEntered(e);
				action1();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseExited(e);
				action2();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				action2();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseReleased(e);
				action1();
			}
			
			private void action1() {
				// TODO Auto-generated method stub

				try {
					int width=50;
					BufferedImage img2=new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
					Image tem_img2=ImageIO.read(new File("resource/icon/setting_128px.png"));
					Graphics2D g2=(Graphics2D)img2.getGraphics();
					g2.setColor(bg);
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.fillRect(0, 0, width,width);
					g2.drawImage(tem_img2.getScaledInstance(width, width, Image.SCALE_SMOOTH), 0,0, null);
//					m_setting_label=new JLabel(new ImageIcon(img2),JLabel.CENTER);
					m_setting_label.setIcon(new ImageIcon(img2));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
			}
			
			private void action2() {
				// TODO Auto-generated method stub
				try {
					int width=50;
					BufferedImage img2=new BufferedImage(width, width,BufferedImage.TYPE_INT_RGB);
					Image tem_img2=ImageIO.read(new File("resource/icon/setting_128px.png"));
					Graphics2D g2=(Graphics2D)img2.getGraphics();
					g2.setColor(bg);
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.fillRect(0, 0, width,width);
					g2.drawImage(tem_img2.getScaledInstance(width-4, width-4, Image.SCALE_SMOOTH), 2,2, null);
					m_setting_label.setIcon(new ImageIcon(img2));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
			}
			
		});
		
		m_scan_host_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				if(!m_send_btn.isEnabled()){
					return;
				}
				scanHost();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseEntered(e);
				m_scan_host_btn.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseExited(e);
				m_scan_host_btn.setBorder(BorderFactory.createLineBorder(Color.white, 1));
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				m_scan_host_btn.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseReleased(e);
				m_scan_host_btn.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			}
			
		});
		
		m_choose_file_btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				int i=m_file_chooser.showOpenDialog(MainFrame.this);
				if(i==JFileChooser.APPROVE_OPTION){
					File file=m_file_chooser.getSelectedFile();
					m_send_path_field.setText(file.getAbsolutePath());
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				m_choose_file_btn.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseReleased(e);
				m_choose_file_btn.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseEntered(e);
				m_choose_file_btn.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseExited(e);
				m_choose_file_btn.setBorder(BorderFactory.createLineBorder(Color.white, 1));
			}
		});
		
		m_send_btn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				String name=(String)m_send_ip_field.getSelectedItem();
				String ip=m_host_map.get(name);
				String filePath=m_send_path_field.getText();				
				int port=TransferManager.port;
				if(ip==null){
					ip=name;
				}
				if(!ip.matches("\\d{1,3}((.\\d{1,3}){3})")){
					JOptionPane.showMessageDialog(MainFrame.this, "找不到设备！");
					return;
				}
				
				File file=new File(filePath);
				if(!file.exists()){
					JOptionPane.showMessageDialog(MainFrame.this, "文件不存在！");
					return ;
				}

				try {
					SendThread sendThread=new SendThread(ip, port, file);
					new SendDialog(MainFrame.this, sendThread);
				} catch (Exception e2) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(MainFrame.this, "发送文件失败！");
				}
			}
		});
		
		m_clear_log_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				m_log_area.setText("");
				int i=JOptionPane.showConfirmDialog(MainFrame.this, "同时清空日志文件？", "清空日志", JOptionPane.YES_NO_OPTION);
				if(i==JOptionPane.YES_OPTION){
					mContext.clearLog();
				}
			}
		});
		
	}
	
	private void scanHost() {
		// TODO Auto-generated method stub
		
		m_send_btn.setEnabled(false);
		m_send_ip_field.setEditable(false);
		m_send_ip_field.setModel(new DefaultComboBoxModel<String>(new String[]{"正在扫描设备..."}));
		
		new SearchTool(m_search_end_oper, mContext.getIp()).searchNetMap();
	}
	
	private void updataScan() {
		// TODO Auto-generated method stub
		Set<String> keySet= m_host_map.keySet();
		int size=keySet.size();
		String[] keyArr=new String[size+1];
		keyArr[0]="选择设备";
		int i=1;
		for(String s:keySet){
			keyArr[i++]=s;
		}
		m_send_ip_field.setModel(new DefaultComboBoxModel<String>(keyArr));
	}
	
	/**
	 * 更新本机信息
	 */
	public void updateHostInfo(String host_name,String host) {
		m_name_field.setText(host_name);
		m_address_field.setText(host);		
		m_tip_host_lable.setText("<html>2、发送文件时选择设备：<font color='red'>"+mContext.getHostName()+"</font></html>");
	}
	
	/**
	 * 更新设置的内容
	 */
	public void updateSetting(String ip,int port,String filePath,String name) {
		// TODO Auto-generated method stub
		m_address_field.setText(mContext.getIp());
		m_name_field.setText(mContext.getHostName());
	}
	
	/**
	 * 返回控制器
	 * @return 返回TransferManager对象
	 */
	public TransferManager getTransferManager() {
		return mContext;
	}
	
	/**
	 * 添加日志
	 * @param log 日志信息
	 */
	public synchronized void addLog(String log) {
		StringBuffer buf=new StringBuffer(m_log_area.getText());
		buf.append("\n");
		buf.append(log);
		m_log_area.setText(buf.toString());
		DefaultCaret caret=(DefaultCaret)m_log_area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		m_log_scroll_pane.setViewportView(m_log_area);
	}

}






































