package transfer.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.util.HashMap;
import java.util.List;

import transfer.control.TransferManager;

public class SettingDialog extends JDialog {
	
	//设置主机信息
	private JLabel m_ip_label;
	private JComboBox<String> m_ip_combo;
	private JLabel m_host_name_label;
	private JTextField m_host_name_field;
	
	//设置文件保存路径
	private JLabel m_save_path_label;
	private JTextField m_save_path_field;
	private JButton m_choose_btn;
	private JButton m_default_btn;
	
	//文件对话框
	private JFileChooser m_file_chooser;
	
	//关于
	private JLabel m_about_text_label;
	
	//卡片式布局器
	private CardLayout m_card_layout;
	private JPanel m_card_panel;
	private JLabel m_base_label;	//基本设置
	private JLabel m_about_label;	//关于
	private MouseAdapter m_mouse_adapter;	//事件处理适配器
	
	//保存按钮
	private JButton m_save_setting_btn;
	private JButton m_cancel_btn;
	
	//父窗口
	private MainFrame mFrame;
	
	//控制器上下文
	private TransferManager m_manager;
	
	/**
	 * 构造函数
	 * @param mFrame 父窗口
	 */
	public SettingDialog(MainFrame mFrame) {
		// TODO Auto-generated constructor stub
		super(mFrame, true);
		this.mFrame=mFrame;
		m_manager=mFrame.getTransferManager();
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
		
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screen=kit.getScreenSize();
		Dimension preferredSize=new Dimension(getPreferredSize().width+50,getPreferredSize().height+100);
		this.setTitle("设置");
		this.setSize(preferredSize);
		this.setLocation((screen.width-preferredSize.width)/2, (screen.height-preferredSize.height)/2);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setResizable(false);
		
	}
	
	private void initLayout(){
		// TODO Auto-generated method stub
		Color bg=new Color(27, 202, 234);
		
		JPanel left_panel=new JPanel();
		JPanel v=new JPanel(new GridLayout(3,1));
		v.add(m_base_label);
		v.add(m_about_label);
		v.setBackground(Color.white);
		left_panel.setBackground(Color.white);
		left_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, bg));
		left_panel.add(v);
		
		this.add(left_panel,BorderLayout.WEST);
		
		JPanel right_panel=new JPanel(new BorderLayout());
		
		m_card_layout=new CardLayout();
		m_card_panel=new JPanel(m_card_layout);
		m_card_panel.add("基本设置", builtCard0());
		m_card_panel.add("关于", builtCard2());		
		right_panel.add(m_card_panel);
		
		JPanel save_panel=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
		save_panel.setBackground(Color.white);
		save_panel.setBorder(BorderFactory.createLineBorder(Color.white, 10));
		save_panel.add(m_cancel_btn);
		save_panel.add(m_save_setting_btn);
		right_panel.add(save_panel,BorderLayout.SOUTH);
		
		this.add(right_panel);
		
	}
	
	private void initData() {
		// TODO Auto-generated method stub

		Color bg=new Color(27, 202, 234);
		
		m_base_label=new JLabel("基本设置",JLabel.CENTER);
		m_base_label.setBorder(BorderFactory.createMatteBorder(5, 10, 5, 10, bg));
		m_base_label.setOpaque(true);
		m_base_label.setBackground(bg);
		m_base_label.setForeground(Color.white);
		
		m_about_label=new JLabel("关于",JLabel.CENTER);
		m_about_label.setBorder(BorderFactory.createMatteBorder(5, 10, 5, 10, Color.white));
		m_about_label.setOpaque(true);
		m_about_label.setBackground(Color.white);
		m_about_label.setForeground(Color.darkGray);	
		
		m_host_name_label=new JLabel("名称：");
		m_host_name_field=new JTextField();
		
		m_ip_label=new JLabel("地址：");
		m_ip_combo=new JComboBox<String>();
		m_ip_combo.setBackground(Color.white);
		m_ip_combo.setEditable(true);
		
		m_save_path_label=new JLabel("保存路径：");
		m_save_path_field=new JTextField(25);
		m_save_path_field.setEditable(false);
		m_choose_btn=new JButton("更改");
		m_default_btn=new JButton("默认");
		m_file_chooser=new JFileChooser(m_save_path_field.getText());
		m_file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		String about="<html>"
				+ "<p>易传 V1.00</p>"
				+ "<p>中国软件杯</p>"
				+ "<p>肥猫小组</p>"
				+ "</html>";
		m_about_text_label=new JLabel(about,JLabel.CENTER);
		m_about_text_label.setForeground(Color.gray);
		
		m_cancel_btn=new JButton("取消");
		m_save_setting_btn=new JButton("应用");
		
		updateSetting();
		
	}
	
	/**
	 * 初始化设置对话框数据
	 */
	private void updateSetting() {
		// TODO Auto-generated method stub
		List<String> ipList=TransferManager.getLocalIPList();
		if(ipList!=null){
			setIPList(m_manager.getIpArray(ipList));
		}
		setFileSavePath(m_manager.getFileSavePath());
		setHostName(m_manager.getHostName());
	}
	
	private JPanel builtCard0() {
		// TODO Auto-generated method stub
		JPanel card0=new JPanel(new BorderLayout());
		Box box=Box.createVerticalBox();
		
		JPanel content=new JPanel(new GridBagLayout());
		GridBagLayout gbl=new GridBagLayout();
		GridBagConstraints s=new GridBagConstraints();
		content.setLayout(gbl);
		content.setBackground(Color.white);
		
		s.gridx=0;
		s.gridy=0;
		s.anchor=GridBagConstraints.EAST;
		s.fill=GridBagConstraints.NONE;
		s.insets=new Insets(15, 0, 0, 0);
		gbl.setConstraints(m_host_name_label, s);	
		content.add(m_host_name_label);	
		
		s.gridx=1;
		s.gridy=0;
		s.anchor=GridBagConstraints.WEST;
		s.fill=GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(m_host_name_field, s);
		content.add(m_host_name_field);
		
		s.gridx=0;
		s.gridy=1;
		s.anchor=GridBagConstraints.EAST;
		s.fill=GridBagConstraints.NONE;
		s.insets=new Insets(15, 0, 15, 0);
		gbl.setConstraints(m_ip_label, s);	
		content.add(m_ip_label);		
		
		s.gridx=1;
		s.gridy=1;
		s.anchor=GridBagConstraints.WEST;
		s.fill=GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(m_ip_combo, s);
		content.add(m_ip_combo);
		
		content.setBorder(BorderFactory.createTitledBorder("常规"));
		
		JPanel tem=new JPanel();
		
		JPanel vb=new JPanel(new GridLayout(2,1));		
		JPanel jp1=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jp1.setBackground(Color.white);
		jp1.add(m_save_path_label);
		jp1.add(m_save_path_field);
		
		JPanel jp2=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jp2.setBackground(Color.white);
		jp2.add(m_default_btn);
		jp2.add(m_choose_btn);
		
		vb.setBackground(Color.white);
		vb.setBorder(BorderFactory.createMatteBorder(10, 0, 10, 0, Color.white));
		vb.add(jp1);
		vb.add(jp2);		
		tem.add(vb);
		tem.setBackground(Color.white);
		tem.setBorder(BorderFactory.createTitledBorder("接收文件设置"));
		
		box.add(content);
		box.add(tem);

		card0.add(box,BorderLayout.NORTH);
		card0.setBackground(Color.white);
		return card0;
	}
	
	private JPanel builtCard2() {
		// TODO Auto-generated method stub
		JPanel card2=new JPanel(new GridBagLayout());
		
		JLabel img_label=new JLabel(new ImageIcon("resource/icon/ic_app.png"),JLabel.CENTER);
		JPanel tem=new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
		
		tem.add(img_label);
		tem.add(m_about_text_label);
		tem.setBackground(Color.white);
		
		card2.add(tem);
		card2.setBackground(Color.white);
		return card2;
	}
	
	@Override
	public void setVisible(boolean arg0) {
		// TODO Auto-generated method stub
		m_host_name_field.setText(m_manager.getHostName());
		m_save_path_field.setText(m_manager.getFileSavePath());
		super.setVisible(arg0);
	}
	
	private void setEventListener() {
		// TODO Auto-generated method stub
		m_mouse_adapter=new MouseAdapter() {
			private Color c=new Color(27, 202, 234);
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				JLabel jl=(JLabel)e.getSource();
				String text=jl.getText();

				if(!text.equals("基本设置")){
					m_base_label.setBackground(Color.white);
					m_base_label.setForeground(Color.darkGray);
					m_base_label.setBorder(BorderFactory.createMatteBorder(5, 10, 5, 10, Color.white));
				}

				if(!text.equals("关于")){
					m_about_label.setBackground(Color.white);
					m_about_label.setForeground(Color.darkGray);
					m_about_label.setBorder(BorderFactory.createMatteBorder(5, 10, 5, 10, Color.white));
				}

				jl.setBackground(c);
				jl.setForeground(Color.white);
				jl.setBorder(BorderFactory.createMatteBorder(5, 10, 5, 10, c));
				
				m_card_layout.show(m_card_panel, text);
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseEntered(e);
				JLabel jl=(JLabel)e.getSource();
				if(jl.getBackground().equals(c)){
					return;
				}
				jl.setForeground(c);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseExited(e);
				JLabel jl=(JLabel)e.getSource();
				if(jl.getBackground().equals(c)){
					return;
				}
				jl.setForeground(Color.darkGray);
			}
		};
		
		m_base_label.addMouseListener(m_mouse_adapter);
		m_about_label.addMouseListener(m_mouse_adapter);
		
		m_cancel_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				SettingDialog.this.setVisible(false);
			}
		});
		
		m_save_setting_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				HashMap<String , String > property=new HashMap<String, String>();
				String ip=(String)m_ip_combo.getSelectedItem();
				String name=m_host_name_field.getText();
				String filePath=m_save_path_field.getText();
				if(!ip.matches("\\d{1,3}((.\\d{1,3}){3})")){
					JOptionPane.showMessageDialog(mFrame, "IP地址不正确！");
					return;
				}
				if(!m_manager.getIp().equals(ip)){
					if(!m_manager.changeServerThread(ip, TransferManager.port, filePath, name)){
						return;
					}
				}else{
					m_manager.setHostName(name);
					m_manager.setFileSavePath(filePath);
				}
				
				property.put("file_path", filePath);
				property.put("host_name", name);
				m_manager.saveProperties(property);
				
				mFrame.updateHostInfo(m_manager.getHostName(), m_manager.getIp());
				updateSetting();
				SettingDialog.this.setVisible(false);
			}
		});
		
		m_choose_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int i=m_file_chooser.showOpenDialog(mFrame);
				if(i==JFileChooser.APPROVE_OPTION){
					File file=m_file_chooser.getSelectedFile();
					m_save_path_field.setText(file.getAbsolutePath());
				}
			}
		});
		
		m_default_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				m_save_path_field.setText(m_manager.getDefaultSavePath());
			}
		});
		
	}
	
	/**
	 * 设置ip列表
	 * @param ipList ip列表
	 */
	public void setIPList(String[] ipList) {
		this.m_ip_combo.setModel(new DefaultComboBoxModel<String>(ipList));
	}
	

	/**
	 * 设置文件保存路径
	 * @param path 文件路径
	 */
	public void setFileSavePath(String path){
		this.m_save_path_field.setText(path);
	}
	
	public void setHostName(String name){
		m_host_name_field.setText(name);
	}
	
}





































