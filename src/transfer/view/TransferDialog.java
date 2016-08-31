package transfer.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class TransferDialog extends JDialog {


	
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	
	//文件名
	protected JLabel m_file_name_label;
	
	//文件大小
	protected JLabel m_file_length_label;
	
	//传输速度
	protected JLabel m_transfer_rate_label;
	
	//进度条
	protected JProgressBar m_progress_bar;
	
	//操作按钮
	protected JButton m_transfer_btn;
	
	//父窗口
	protected MainFrame m_frame;
	
	public TransferDialog(MainFrame frame) {
		// TODO Auto-generated constructor stub
		super(frame, false);
		this.m_frame=frame;
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		initData();
		initLayout();
		
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screen=kit.getScreenSize();
		Dimension preferredSize=new Dimension(450, 180);
		
		this.setSize(preferredSize);
		this.setMinimumSize(preferredSize);
		this.setResizable(false);
		this.setLocation((screen.width-preferredSize.width)/2, (screen.height-preferredSize.height)/2);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		
	}
	
	private void initLayout() {
		// TODO Auto-generated method stub
		
		Box layout=Box.createVerticalBox();
				
		JPanel name_panel=new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
		name_panel.setBackground(Color.white);
		name_panel.add(m_file_name_label);
		layout.add(name_panel);
		
		JPanel length_panel=new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
		length_panel.setBackground(Color.white);
		length_panel.add(m_file_length_label);
		length_panel.add(m_transfer_rate_label);
		layout.add(length_panel);
		
		JPanel progress_panel=new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
		progress_panel.setBackground(Color.white);
		progress_panel.add(m_progress_bar);
		progress_panel.add(m_transfer_btn);
		layout.add(progress_panel);
		
		JPanel tem=new JPanel();
		tem=new JPanel(new GridBagLayout());
		tem.add(layout);
		tem.setBackground(Color.white);
		tem.setBorder(BorderFactory.createMatteBorder(20, 0, 20, 0, Color.white));
		this.add(tem);
		
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		
		Font font=new Font("宋体",Font.PLAIN,16);
		
		m_file_name_label=new JLabel("文件名");
		m_file_name_label.setFont(font);
		
		m_file_length_label=new JLabel("文件大小");
		m_file_length_label.setFont(font);
		
		m_transfer_rate_label=new JLabel("传输速度");
		m_transfer_rate_label.setFont(font);
		
		m_progress_bar=new JProgressBar();
		m_progress_bar.setPreferredSize(new Dimension(300,25));
		m_progress_bar.setForeground(Color.green);
		m_progress_bar.setBackground(Color.white);
		m_progress_bar.setStringPainted(true);
		
		m_transfer_btn=new JButton("按钮");
	}

	
	protected String getLengthString(long length) {
		// TODO Auto-generated method stub
		DecimalFormat df=new DecimalFormat("0.0");
		double tem=length;
		tem/=1024.0;
		if(1024.0-tem>0.0000000001){
			return df.format(tem)+"KB";
		}
		tem/=1024.0;
		if(1024.0-tem>0.0000000001){
			return df.format(tem)+"MB";
		}
		tem/=1024.0;
		return df.format(tem)+"GB";
		
	}
}














































